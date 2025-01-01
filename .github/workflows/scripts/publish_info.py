"""
A script to scan through all valid submodules in build-artifacts.zip/$module/$version/gradle.properties,
and generate the information needed for mc-publish
"""
__author__ = 'Hendrix_Shen'

import os
from typing import List

import common
from common import Module


def format_multiple_line(var: str, raw: str) -> str:
    return (f'{var}<<EOF' + '\n' +
            f'{raw}' + '\n' +
            'EOF' + '\n')


def main():
    platform = os.environ.get('MOD_PLATFORM', '')
    mc_ver = os.environ.get('MOD_MC_VER', '')

    if platform == '' or mc_ver == '':
        raise RuntimeError('MOD_PLATFORM or MOD_MC_VER not set')

    # Temply hard coding here.
    loader_list: List[str]

    if platform == 'fabric':
        loader_list = ['fabric', 'quilt']
    elif platform == 'forge':
        loader_list = ['forge']
    elif platform == 'neoforge':
        loader_list = ['neoforge']
    else:
        raise RuntimeError(f'Unknown platform: {platform}')

    settings: dict = common.get_settings()
    dependencies_list: List[str] = []
    mc_ver_list: List[str] = []

    for version in settings['versions']:
        module: Module = Module.of(version)

        if module.mc_ver() != mc_ver or module.platform() != platform:
            continue

        proj_path = f'./versions/{version}/gradle.properties'
        dependencies_list.extend(
            [s for s in common.read_prop(proj_path, 'publish.dependencies_list').split(',') if s.strip() != ''])
        mc_ver_list.extend(
            [s for s in common.read_prop(proj_path, 'publish.game_version').split(',') if s.strip() != ''])

    publish_dependencies: str = '\n'.join(sorted(list(set(dependencies_list))))
    publish_game_versions: str = '\n'.join(sorted(list(set(mc_ver_list))))
    publish_loaders: str = '\n'.join(sorted(list(set(loader_list))))

    with open(os.environ['GITHUB_OUTPUT'], 'w') as f:
        f.write(format_multiple_line('publish_dependencies', publish_dependencies))
        f.write(format_multiple_line('publish_game_versions', publish_game_versions))
        f.write(format_multiple_line('publish_loaders', publish_loaders))


if __name__ == '__main__':
    main()
