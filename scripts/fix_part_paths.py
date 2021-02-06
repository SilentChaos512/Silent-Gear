import os
import re

root = '../src/main/resources/data/silentgear/silentgear/parts'

for path in os.listdir(root):
    if not path.endswith('.json'):
        continue
    print(path)
    type_name = re.search(r'^[a-z]+(?=_)', path).group()
    part_name = re.search(r'(?<=_)[a-z0-9_]+(?=.json$)', path).group()
    new_path = type_name + os.sep + part_name + '.json'
    print(path + ' -> ' + new_path)

    full_src = os.path.join(root, path)
    full_dst = os.path.join(root, new_path)
    new_dir = os.path.join(root, type_name)
    if not os.path.exists(new_dir):
        os.makedirs(new_dir)
    os.rename(full_src, full_dst)
