from enum import Enum
import io
import json
import os


class PartType(Enum):
    ROD = 1
    HEAD = 2
    TIP = 4
    BOWSTRING = 8


default_textures = {
    PartType.ROD: 'rod_generic_lc',
    PartType.HEAD: 'head_generic_hc',
    PartType.TIP: 'tip_iron',
    PartType.BOWSTRING: 'bowstring_string',
}

blank_texture = 'silentgear:item/blank'

tool_models = [
    [PartType.ROD],
    [PartType.HEAD],
    [PartType.ROD, PartType.HEAD],
    [PartType.HEAD, PartType.TIP],
    [PartType.ROD, PartType.HEAD, PartType.TIP],
]

bow_models = [
    [PartType.ROD],
    [PartType.HEAD],
    [PartType.ROD, PartType.HEAD],
    [PartType.HEAD, PartType.TIP],
    [PartType.ROD, PartType.HEAD, PartType.TIP],
    [PartType.ROD, PartType.HEAD, PartType.BOWSTRING],
    [PartType.HEAD, PartType.TIP, PartType.BOWSTRING],
    [PartType.ROD, PartType.HEAD, PartType.TIP, PartType.BOWSTRING],
]

tool_textures = [
    'rod_generic_lc',
    'head_generic_hc',
    '_highlight'
]

bow_textures = [
    'rod_generic_lc',
    'head_generic_hc',
    '_highlight',
    'bowstring_string_0'
]

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

bows = [
    'bow',
    'slingshot'
]


def model_id(part_types):
    return sum((t.value for t in part_types))


def model_name(tool: str, part_types):
    return 'silentgear:item/' + tool + '/' + '_'.join((p.name.lower() for p in part_types))


def tex_name(tool: str, tex: str, frame=-1):
    if not tex:
        return blank_texture
    frame_str = '' if frame < 0 else '_' + str(frame)
    return 'silentgear:item/{0}/{1}{2}'.format(tool, tex, frame_str)


def make_model_override(tool: str, part_types):
    return {
        "predicate": {
            "silentgear:model_type": model_id(part_types)
        },
        "model": model_name(tool, part_types)
    }


def make_root_model(tool: str, models, textures):
    print(tool + ' root model')
    tex = {'layer' + str(i): tex_name(tool, textures[i])
           for i in range(len(textures))}
    overrides = [make_model_override(tool, parts) for parts in models]

    data = {
        "__comment__": "Lite gear model",
        "parent": "item/handheld",
        "textures": tex,
        "overrides": overrides
    }
    write_json(tool, data)


def make_model(tool: str, model: str, *textures):
    print(tool + '_' + model, textures)
    tex = {'layer' + str(i): tex_name(tool, textures[i])
           for i in range(len(textures))}
    data = {
        "__comment__": "Lite gear model",
        "parent": 'item/handheld',
        "textures": tex
    }
    write_json(tool + '/' + model, data)


def is_valid_bow_configuration(model_type):
    return PartType.ROD in model_type and PartType.HEAD in model_type and PartType.BOWSTRING in model_type


def make_bow_overrides(tool: str):
    ret = []
    for model_type in bow_models:
        model = model_name(tool, model_type)
        ret.append({
            "predicate": {
                "silentgear:model_type": model_id(model_type)
            },
            "model": model
        })
        if is_valid_bow_configuration(model_type):
            ret.append({
                "predicate": {
                    "silentgear:model_type": model_id(model_type),
                    "pulling": 1
                },
                "model": model + '_1'
            })
            ret.append({
                "predicate": {
                    "silentgear:model_type": model_id(model_type),
                    "pulling": 1,
                    "pull": 0.65
                },
                "model": model + '_2'
            })
            ret.append({
                "predicate": {
                    "silentgear:model_type": model_id(model_type),
                    "pulling": 1,
                    "pull": 0.9
                },
                "model": model + '_3'
            })
    return ret


def make_bow_root_model(tool: str, model: str, textures):
    print(tool + ' root model')
    tex = {'layer' + str(i): tex_name(tool, textures[i])
           for i in range(len(textures))}
    overrides = make_bow_overrides(tool)
    data = {
        "__comment__": "Lite gear model",
        "parent": 'item/bow',
        "textures": tex,
        "overrides": overrides
    }
    write_json(tool, data)


def write_json(filename, data):
    with open('output/' + filename + '.json', 'w', encoding='utf8') as f:
        json.dump(data, f, indent=2)


if __name__ == '__main__':
    for tool in tools:
        if not os.path.exists('output/' + tool):
            os.makedirs('output/' + tool)

        rod = 'rod_generic_lc'
        head = 'head_generic_hc'
        tip = 'tip_iron'
        shine = '_highlight'

        make_root_model(tool, tool_models, tool_textures)
        if (tool == 'sword'):
            guard = 'guard_generic_hc'
            # rod, head, tip, guard, shine
            make_model(tool, 'head', None, head, None, guard, shine)
            make_model(tool, 'rod_head', rod, head, None, guard, shine)
            make_model(tool, 'rod', rod, None, None, guard)
            make_model(tool, 'head_tip', None, head, tip, guard, shine)
            make_model(tool, 'rod_head_tip', rod, head, tip, guard, shine)
        else:
            # rod, head, tip, shine
            make_model(tool, 'rod', rod)
            make_model(tool, 'head', None, head, shine)
            make_model(tool, 'rod_head', rod, head, shine)
            make_model(tool, 'head_tip', None, head, tip, shine)
            make_model(tool, 'rod_head_tip', rod, head, tip, shine)

    for bow in bows:
        if not os.path.exists('output/' + bow):
            os.makedirs('output/' + bow)

        rod = default_textures[PartType.ROD]
        head = default_textures[PartType.HEAD]
        tip = default_textures[PartType.TIP]
        shine = '_highlight'
        bowstring = default_textures[PartType.BOWSTRING] + '_0'

        make_bow_root_model(bow, bow_models, bow_textures)
        # Incomplete tools cannot be fired (and thus are not animated)
        make_model(bow, 'rod', rod)
        make_model(bow, 'head', None, head, shine)
        make_model(bow, 'rod_head', rod, head, shine)
        make_model(bow, 'head_tip', None, head, tip, shine)
        make_model(bow, 'rod_head_tip', rod, head, tip, shine)
        make_model(bow, 'head_bowstring', None, head, shine, bowstring)
        make_model(bow, 'head_tip_bowstring',
                   None, head, tip, shine, bowstring)
        # Animated base models
        make_model(bow, 'rod_head_bowstring', rod, head, shine, bowstring)
        make_model(bow, 'rod_head_tip_bowstring',
                   rod, head, tip, shine, bowstring)

        for frame in range(4):
            bowstring = default_textures[PartType.BOWSTRING] + '_' + str(frame)
            if (frame == 3 and bow == 'bow'):
                head = head + '_3'
                tip = tip + '_3'
            # rod, head, tip, shine, bowstring
            make_model(bow, 'rod_head_bowstring_' + str(frame),
                       rod, head, shine, bowstring)
            make_model(bow, 'rod_head_tip_bowstring_' + str(frame),
                       rod, head, tip, shine, bowstring)
