__author__ = 'Hendrix_Shen'

import os
from typing import List, Dict

import common


def main():
    target_subproject_env = os.environ.get('TARGET_SUBPROJECT', '')
    target_subprojects = list(filter(None, target_subproject_env.split(',') if target_subproject_env != '' else []))
    subproject_dict: Dict[str, List[str]] = common.get_subproject_dict()
    gradle_subprojects: List[str] = []

    for target_subproject in target_subprojects:
        for platform in subproject_dict:
            if target_subproject in subproject_dict[platform]:
                gradle_subprojects.append('{}-{}'.format(target_subproject, platform))

    result: str = ','.join(gradle_subprojects)

    with open(os.environ['GITHUB_STEP_SUMMARY'], 'w') as f:
        f.write('## Determining subprojects\n')
        f.write('- subprojects={}\n'.format(result))

    with open(os.environ['GITHUB_OUTPUT'], 'w') as f:
        f.write('- subprojects={}\n'.format(result))


if __name__ == '__main__':
    main()
