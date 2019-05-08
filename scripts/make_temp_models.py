import io
import json


tools = [
    'axe',
    'dagger',
    'excavator',
    'hammer',
    'katana',
    'machete',
    'mattock',
    'pickaxe',
    'shovel',
    'sickle',
    'spear',
    'sword',
]


def make_root_model(tool: str):
    print(tool + ' root model')
    data = {
        "__comment__": "Lite gear model",
        "parent": "item/handheld",
        "textures": {
            "layer0": "silentgear:item/{0}/rod_generic_lc".format(tool),
            "layer1": "silentgear:item/{0}/head_generic_hc".format(tool),
            "layer2": "silentgear:item/{0}/_highlight".format(tool)
        },
        "overrides": [
            {
                "predicate": {
                    "silentgear:model_type": 1
                },
                "model": "silentgear:item/{0}_rod".format(tool)
            },
            {
                "predicate": {
                    "silentgear:model_type": 2
                },
                "model": "silentgear:item/{0}_head".format(tool)
            },
            {
                "predicate": {
                    "silentgear:model_type": 3
                },
                "model": "silentgear:item/{0}_head_rod".format(tool)
            },
            {
                "predicate": {
                    "silentgear:model_type": 6
                },
                "model": "silentgear:item/{0}_tip_head".format(tool)
            },
            {
                "predicate": {
                    "silentgear:model_type": 7
                },
                "model": "silentgear:item/{0}_tip_head_rod".format(tool)
            }
        ]
    }
    write_json(tool, data)


def make_model(tool: str, model: str, *layers):
    print(tool + '_' + model, layers)
    textures = {}
    for i in range(len(layers)):
        textures['layer' + str(i)] = 'silentgear:item/' + layers[i]
    data = {
        "__comment__": "Lite gear model",
        "parent": 'item/handheld',
        "textures": textures
    }
    write_json(tool + '_' + model, data)


def write_json(filename, data):
    with open('output/' + filename + '.json', 'w', encoding='utf8') as f:
        json.dump(data, f, indent=2)


if __name__ == '__main__':
    for tool in tools:
        blank = 'blank'
        rod = tool + '/rod_generic_lc'
        head = tool + '/head_generic_hc'
        tip = tool + '/tip_iron'
        shine = tool + '/_highlight'

        make_root_model(tool)
        if (tool == 'sword'):
            guard = tool + '/guard_generic_hc'
            # rod, head, tip, guard, shine
            make_model(tool, 'head', blank, head, blank, guard, shine)
            make_model(tool, 'head_rod', rod, head, blank, guard, shine)
            make_model(tool, 'rod', rod, blank, blank, guard)
            make_model(tool, 'tip_head', blank, head, tip, guard, shine)
            make_model(tool, 'tip_head_rod', rod, head, tip, guard, shine)
        else:
            # rod, head, tip, shine
            make_model(tool, 'head', blank, head, shine)
            make_model(tool, 'head_rod', rod, head, shine)
            make_model(tool, 'rod', rod)
            make_model(tool, 'tip_head', blank, head, tip, shine)
            make_model(tool, 'tip_head_rod', rod, head, tip, shine)
