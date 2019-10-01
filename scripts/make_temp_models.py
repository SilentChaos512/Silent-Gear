from enum import Enum
import io
import json
import os


tools = [
    'axe',
    'dagger',
    'excavator',
    'hammer',
    'katana',
    'lumber_axe',
    'machete',
    'mattock',
    'pickaxe',
    'shovel',
    'sickle',
    'spear',
    'sword',
]

bows = {
    'bow': 'item/bow',
    'crossbow': 'item/crossbow',
    'slingshot': 'item/bow'
}

tool_models = [
    # rod
    {'rod': 1},
    {'rod': 2},
    # head
    {'head': 1},
    {'head': 2},
    {'head': 3},
    # rod, head
    {'rod': 1, 'head': 1},
    {'rod': 2, 'head': 1},
    {'rod': 1, 'head': 2},
    {'rod': 2, 'head': 2},
    {'rod': 1, 'head': 3},
    {'rod': 2, 'head': 3},
    # head, tip
    {'head': 1, 'tip': 2},
    {'head': 2, 'tip': 2},
    {'head': 3, 'tip': 2},
    # rod, grip, head
    {'rod': 1, 'grip': 1, 'head': 1},
    {'rod': 2, 'grip': 1, 'head': 1},
    {'rod': 1, 'grip': 1, 'head': 2},
    {'rod': 2, 'grip': 1, 'head': 2},
    {'rod': 1, 'grip': 1, 'head': 3},
    {'rod': 2, 'grip': 1, 'head': 3},
    # rod, head, tip
    {'rod': 1, 'head': 1, 'tip': 2},
    {'rod': 2, 'head': 1, 'tip': 2},
    {'rod': 1, 'head': 2, 'tip': 2},
    {'rod': 2, 'head': 2, 'tip': 2},
    {'rod': 1, 'head': 3, 'tip': 2},
    {'rod': 2, 'head': 3, 'tip': 2},
    # rod, grip, head, tip
    {'rod': 1, 'grip': 1, 'head': 1, 'tip': 2},
    {'rod': 2, 'grip': 1, 'head': 1, 'tip': 2},
    {'rod': 1, 'grip': 1, 'head': 2, 'tip': 2},
    {'rod': 2, 'grip': 1, 'head': 2, 'tip': 2},
    {'rod': 1, 'grip': 1, 'head': 3, 'tip': 2},
    {'rod': 2, 'grip': 1, 'head': 3, 'tip': 2},
]

bow_models = [
    # rod
    {'rod': 1},
    {'rod': 2},
    # head
    {'head': 1},
    {'head': 2},
    {'head': 3},
    # rod, head
    {'rod': 1, 'head': 1},
    {'rod': 2, 'head': 1},
    {'rod': 1, 'head': 2},
    {'rod': 2, 'head': 2},
    {'rod': 1, 'head': 3},
    {'rod': 2, 'head': 3},
    # head, tip
    {'head': 1, 'tip': 2},
    {'head': 2, 'tip': 2},
    {'head': 3, 'tip': 2},
    # rod, head, tip
    {'rod': 1, 'head': 1, 'tip': 2},
    {'rod': 2, 'head': 1, 'tip': 2},
    {'rod': 1, 'head': 2, 'tip': 2},
    {'rod': 2, 'head': 2, 'tip': 2},
    {'rod': 1, 'head': 3, 'tip': 2},
    {'rod': 2, 'head': 3, 'tip': 2},
    # rod, head, bowstring
    {'rod': 1, 'head': 1, 'bowstring': 1},
    {'rod': 2, 'head': 1, 'bowstring': 1},
    {'rod': 1, 'head': 2, 'bowstring': 1},
    {'rod': 2, 'head': 2, 'bowstring': 1},
    {'rod': 1, 'head': 3, 'bowstring': 1},
    {'rod': 2, 'head': 3, 'bowstring': 1},
    # head, tip, bowstring
    {'head': 1, 'tip': 2, 'bowstring': 1},
    {'head': 1, 'tip': 2, 'bowstring': 1},
    {'head': 2, 'tip': 2, 'bowstring': 1},
    {'head': 2, 'tip': 2, 'bowstring': 1},
    {'head': 3, 'tip': 2, 'bowstring': 1},
    {'head': 3, 'tip': 2, 'bowstring': 1},
    # rod, head, tip, bowstring
    {'rod': 1, 'head': 1, 'tip': 2, 'bowstring': 1},
    {'rod': 2, 'head': 1, 'tip': 2, 'bowstring': 1},
    {'rod': 1, 'head': 2, 'tip': 2, 'bowstring': 1},
    {'rod': 2, 'head': 2, 'tip': 2, 'bowstring': 1},
    {'rod': 1, 'head': 3, 'tip': 2, 'bowstring': 1},
    {'rod': 2, 'head': 3, 'tip': 2, 'bowstring': 1},
]

textures_map = {
    'rod': [None, 'rod_generic_lc', 'rod_generic_hc', None],
    'grip': [None, 'grip_wool', None, None],
    'head': [None, 'head_generic_lc', 'head_generic_hc', 'head_generic_hc'],
    'tip': [None, None, 'tip_iron', None],
    'bowstring': [None, 'bowstring_string', None, None],
}

blank_texture = 'silentgear:item/blank'


def make_root_model(gear_type: str, model_types: dict, parent='item/handheld') -> dict:
    overrides = [make_override(gear_type, model) for model in model_types]

    for model in model_types:
        if is_valid_bow_configuration(model):
            overrides.append(make_override(gear_type, model, frame=1, extra_predicates={'pulling': 1}))
            overrides.append(make_override(gear_type, model, frame=2, extra_predicates={'pulling': 1, 'pull': 0.65}))
            overrides.append(make_override(gear_type, model, frame=3, extra_predicates={'pulling': 1, 'pull': 0.9}))
            if gear_type == 'crossbow':
                overrides.append(make_override(gear_type, model, model_suffix='_arrow',
                                               extra_predicates={'charged': 1}))
                overrides.append(make_override(gear_type, model, model_suffix='_firework',
                                               extra_predicates={'charged': 1, 'firework': 1}))

    return {
        '__comment__': 'Lite gear model',
        'parent': parent,
        'textures': {
            'layer0': 'silentgear:item/' + gear_type + '/rod_generic_lc',
            'layer1': 'silentgear:item/' + gear_type + '/head_generic_hc',
            'layer2': 'silentgear:item/' + gear_type + '/_highlight',
        },
        'overrides': overrides
    }


def make_override(gear_type: str, model: dict, frame=-1, model_suffix='', extra_predicates={}) -> dict:
    predicate_base = {('silentgear:lite_' + key): model[key] for key in model.keys()}
    predicate = {**predicate_base, **extra_predicates}
    return {
        'predicate': predicate,
        'model': model_name(gear_type, model, frame) + model_suffix
    }


def make_gear_model(gear_type: str, model_type: dict, frame=-1, loaded_ammo: str = None) -> dict:
    texture_list = make_texture_list(gear_type, model_type, frame, loaded_ammo)
    textures = {'layer' + str(i): texture_list[i] for i in range(len(texture_list))}
    if 'head' in model_type and model_type['head'] == 3:
        textures['layer' + str(len(textures))] = tex_name(gear_type, '_highlight')
    return {
        '__comment__': 'Lite gear model',
        'parent': 'silentgear:item/' + gear_type,
        'textures': textures
    }


def make_texture_list(gear_type: str, model: dict, frame=-1, loaded_ammo: str = None) -> list:
    textures = []
    for tex in textures_map.keys():
        if tex in model:
            if tex == 'bowstring' and loaded_ammo:
                textures.append(tex_name(gear_type, 'bowstring_string_' + loaded_ammo))
            else:
                textures.append(tex_name(gear_type, textures_map[tex][model[tex]], frame))
        else:
            textures.append(blank_texture)

    # Remove trailing blanks
    while len(textures) > 0 and textures[-1] == blank_texture:
        textures.pop()

    return textures


def model_name(gear_type: str, model: dict, frame=-1) -> str:
    return 'silentgear:item/' + short_model_name(gear_type, model, frame)


def short_model_name(gear_type: str, model: dict, frame=-1) -> str:
    parts_str = '_'.join(key + str(model[key]) for key in model.keys())
    frame_str = '' if frame < 0 else '_' + str(frame)
    return '{0}/{1}{2}'.format(gear_type, parts_str, frame_str)


def tex_name(gear_type: str, tex: str, frame=-1) -> str:
    if not tex:
        return blank_texture
    frame_str = '' if frame < 0 else '_' + str(frame)
    return 'silentgear:item/{0}/{1}{2}'.format(gear_type, tex, frame_str)


def is_valid_bow_configuration(model_type):
    return 'rod' in model_type and 'head' in model_type and 'bowstring' in model_type


def write_json(filename, data):
    with open('output/' + filename + '.json', 'w', encoding='utf8') as f:
        json.dump(data, f, indent=2)


if __name__ == '__main__':
    # Tools, melee weapons
    for tool in tools:
        if not os.path.exists('output/' + tool):
            os.makedirs('output/' + tool)

        write_json(tool, make_root_model(tool, tool_models))
        for model in tool_models:
            write_json(short_model_name(tool, model), make_gear_model(tool, model))

    # Ranged weapons
    for bow, parent in bows.items():
        if not os.path.exists('output/' + bow):
            os.makedirs('output/' + bow)

        write_json(bow, make_root_model(bow, bow_models, parent=parent))
        for model in bow_models:
            write_json(short_model_name(bow, model), make_gear_model(bow, model))
            if (is_valid_bow_configuration(model)):
                # Loaded crossbows
                if bow == 'crossbow':
                    for ammo in ['arrow', 'firework']:
                        model_data = make_gear_model(bow, model, frame=3, loaded_ammo=ammo)
                        write_json(short_model_name(bow, model) + '_' + ammo, model_data)
                # Pulling animation frames
                for i in range(1, 4):
                    write_json(short_model_name(bow, model, frame=i), make_gear_model(bow, model, frame=i))
