"""
Modified from github.com/Fallen-Breath/fabric-mod-template
Originally authored by Fallen_Breath

A script to scan through the versions directory and collect all folder names as the subproject list,
then output a json as the github action include matrix
"""
__author__ = 'Hendrix_Shen'

import json
import os
from typing import Dict, List

import common


def main():
    # target_subproject_env = os.environ.get('TARGET_SUBPROJECT', '')
    target_subproject_env = ''
    target_subprojects = list(filter(None, target_subproject_env.split(',') if target_subproject_env != '' else []))
    print('target_subprojects: {}'.format(target_subprojects))
    subproject_dict: Dict[str, List[str]] = common.get_subproject_dict()
    matrix: Dict[str, List[Dict[str, str]]] = {'include': []}

    if len(target_subprojects) == 0:
        for platform in subproject_dict:
            for mc_ver in subproject_dict[platform]:
                matrix['include'].append({
                    'platform': platform,
                    'mc_ver': mc_ver
                })
    else:
        for platform in subproject_dict:
            for mc_ver in subproject_dict[platform]:
                if mc_ver in target_subprojects:
                    matrix['include'].append({
                        'platform': platform,
                        'mc_ver': mc_ver
                    })

    with open(os.environ['GITHUB_OUTPUT'], 'w') as f:
        f.write('matrix={}\n'.format(json.dumps(matrix)))

    print('matrix:')
    print(json.dumps(matrix, indent=2))


if __name__ == '__main__':
    main()
