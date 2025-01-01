"""
Modified from github.com/Fallen-Breath/fabric-mod-template
Originally authored by Fallen_Breath

A script to scan through all valid mod jars in build-artifacts.zip/$module/$version/build/libs,
and generate an artifact summary table for that to GitHub action step summary
"""
__author__ = 'Hendrix_Shen'

import functools
import glob
import hashlib
import os
from typing import List, Dict

import common
from common import FileData

__EMPTY_FILE_INFO = FileData('*not found*', 0, '*N/A*')


def get_sha256_hash(file_path: str) -> str:
    sha256_hash = hashlib.sha256()

    with open(file_path, 'rb') as f:
        for buf in iter(functools.partial(f.read, 4096), b''):
            sha256_hash.update(buf)

    return sha256_hash.hexdigest()


def get_file_info(file_paths: List[str], subproject: str, warnings: List[str]) -> FileData:
    if len(file_paths) == 0:
        return __EMPTY_FILE_INFO

    if len(file_paths) > 1:
        warnings.append(
            'Found too many build files in subproject {}: {}'.format(subproject, ', '.join(file_paths)))

    return FileData(os.path.basename(file_paths[0]), os.path.getsize(file_paths[0]), get_sha256_hash(file_paths[0]))


def main():
    subproject_dict: Dict[str, List[str]] = common.get_projects_by_platform()

    with open(os.environ['GITHUB_STEP_SUMMARY'], 'w') as f:
        warnings = []
        modules = [
            common.Module(mc_ver, platform)
            for platform in subproject_dict
            for mc_ver in subproject_dict[platform]]

        modules = sorted(list(set(modules)), key=lambda m: (m.mc_ver(), m.platform()))
        f.write('## Build Artifacts Summary\n\n')
        f.write('| Minecraft | Platform | File | Size | SHA-256 |\n')
        f.write('| --- | --- |--- | --- | --- |\n')

        for module in modules:
            file_paths = glob.glob('build-artifacts/versions/{}/build/libs/*.jar'.format(module.get_str()))
            file_paths = list(
                filter(lambda fp: not fp.endswith('-sources.jar') and not fp.endswith('-javadoc.jar'), file_paths))
            file_info = get_file_info(file_paths, 'magiclib-wrapper', warnings)
            f.write('| {} | {} | {} | {} | {} |\n'.format(module.mc_ver(), module.pretty_platform(),
                                                          file_info.file_name, file_info.file_size,
                                                          file_info.sha256))

        if len(warnings) > 0:
            f.write('\n### Warnings\n\n')
            for warning in warnings:
                f.write('- {}\n'.format(warning))


if __name__ == '__main__':
    main()
