"""
A script to valid TARGET_SUBPROJECT.
"""
__author__ = 'Hendrix_Shen'

import os
import sys
from typing import List

import common


def main():
    target_subproject_env = os.environ.get('TARGET_SUBPROJECT', '')
    target_subprojects = list(filter(None, target_subproject_env.split(',') if target_subproject_env != '' else []))
    subproject_dict: dict = common.get_subproject_dict()
    mc_ver: List[str] = common.get_mc_vers(subproject_dict)

    for subproject in target_subprojects:
        if subproject not in mc_ver:
            print('Could not found subproject {} in any platform!'.format(subproject))
            sys.exit(1)


if __name__ == '__main__':
    main()
