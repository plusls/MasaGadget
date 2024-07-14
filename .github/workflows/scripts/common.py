"""
Common functions
"""
__author__ = 'Hendrix_Shen'

import json
from typing import Dict, List, Set

__PLATFORM_MAPPING: dict = {
    'fabric': 'Fabric',
    'forge': 'Forge',
    'neoforge': 'NeoForge',
    'quilt': 'Quilt'
}


def get_mc_vers(subproject_dict: Dict[str, List[str]]) -> List[str]:
    mc_vers: Set[str] = set()

    for subproject in subproject_dict:
        for mc_ver in subproject_dict[subproject]:
            mc_vers.add(mc_ver)

    return sorted(list(mc_vers))


def get_subproject_dict() -> Dict[str, List[str]]:
    with open('settings.json') as f:
        settings: dict = json.load(f)

    projects: Dict[str, List[str]] = {}

    for version in settings['versions']:
        module: Module = Module.of(version)

        if module.platform() not in projects:
            projects[module.platform()] = []

        projects[module.platform()].append(module.mc_ver())

    for platform in projects:
        projects[platform] = sorted(list(set(projects[platform])))

    return projects


def pretty_platform(platform: str) -> str:
    return __PLATFORM_MAPPING.get(platform, '* Unknown *')


def read_prop(file_name: str, key: str) -> str:
    with open(file_name) as prop:
        return next(filter(
            lambda x: x.split('=', 1)[0].strip() == key,
            prop.readlines()
        )).split('=', 1)[1].lstrip()


class Module:
    __mc_ver: str
    __platform: str

    def __init__(self, mc_ver: str, platform: str) -> None:
        self.__mc_ver = mc_ver
        self.__platform = platform

    @staticmethod
    def of(module_name: str) -> 'Module':
        s = module_name.split('-')

        if len(s) == 2:
            return Module(s[0], s[1])
        else:
            return Module('unknown', 'unknown')

    def platform(self) -> str:
        return self.__platform

    def mc_ver(self) -> str:
        return self.__mc_ver

    def pretty_platform(self) -> str:
        return pretty_platform(self.__platform)

    def get_str(self) -> str:
        return f'{self.__mc_ver}-{self.__platform}'
