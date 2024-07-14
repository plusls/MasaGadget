"""
Modified from github.com/Fallen-Breath/fabric-mod-template
Originally authored by Fallen_Breath

A script to scan through all valid mod jars in build-artifacts.zip/$version/build/libs,
and generate an artifact summary table for that to GitHub action step summary
"""
__author__ = 'Hendrix_Shen'

import functools
import glob
import hashlib
import json
import os
from typing import List, Dict

import common


def get_sha256_hash(file_path: str) -> str:
    sha256_hash = hashlib.sha256()

    with open(file_path, 'rb') as f:
        for buf in iter(functools.partial(f.read, 4096), b''):
            sha256_hash.update(buf)

    return sha256_hash.hexdigest()


def get_file_info(file_paths: List[str], subproject: str, warnings: List[str]) -> dict:
    if len(file_paths) == 0:
        file_name = '*not found*'
        file_size = 0
        sha256 = '*N/A*'
    else:
        file_name = '`{}`'.format(os.path.basename(file_paths[0]))
        file_size = '{} B'.format(os.path.getsize(file_paths[0]))
        sha256 = '`{}`'.format(get_sha256_hash(file_paths[0]))
        if len(file_paths) > 1:
            warnings.append(
                'Found too many build files in subproject {}: {}'.format(subproject, ', '.join(file_paths)))

    return {
        'file_name': file_name,
        'file_size': file_size,
        'sha256': sha256
    }


def main():
    target_subproject_env = os.environ.get('TARGET_SUBPROJECT', '')
    target_subprojects = list(filter(None, target_subproject_env.split(',') if target_subproject_env != '' else []))
    print('target_subprojects: {}'.format(target_subprojects))
    subproject_dict: Dict[str, List[str]] = common.get_subproject_dict()

    with open(os.environ['GITHUB_STEP_SUMMARY'], 'w') as f:
        warnings = []
        modules: List[common.Module] = []

        for platform in subproject_dict:
            for mc_ver in subproject_dict[platform]:
                if len(target_subprojects) > 0 and mc_ver not in target_subprojects:
                    print('Skipping {}-{}'.format(mc_ver, platform))
                    continue

                modules.append(common.Module(mc_ver, platform))

        modules = sorted(list(set(modules)), key=lambda m: (m.mc_ver(), m.platform()))
        f.write('## Build Artifacts Summary\n\n')
        f.write('| Minecraft | Platform | File | Size | SHA-256 |\n')
        f.write('| --- | --- |--- | --- | --- |\n')

        for module in modules:
            file_paths = glob.glob('build-artifacts/{}/build/libs/*.jar'.format(module.get_str()))
            file_paths = list(
                filter(lambda fp: not fp.endswith('-sources.jar') and not fp.endswith('-javadoc.jar'), file_paths))
            file_info = get_file_info(file_paths, 'magiclib-wrapper', warnings)
            f.write('| {} | {} | {} | {} | {} |\n'.format(module.mc_ver(), module.pretty_platform(),
                                                          file_info.get('file_name'), file_info.get('file_size'),
                                                          file_info.get('sha256')))

        if len(warnings) > 0:
            f.write('\n### Warnings\n\n')
            for warning in warnings:
                f.write('- {}\n'.format(warning))


if __name__ == '__main__':
    main()
