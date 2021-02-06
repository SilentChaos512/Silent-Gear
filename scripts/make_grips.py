def color_str(color: int):
    return '#' + hex(color)[2:].upper()


colors = {
    "white": 16383998,
    "orange": 16351261,
    "magenta": 13061821,
    "light_blue": 3847130,
    "yellow": 16701501,
    "lime": 8439583,
    "pink": 15961002,
    "gray": 4673362,
    "light_gray": 10329495,
    "cyan": 1481884,
    "purple": 8991416,
    "blue": 3949738,
    "brown": 8606770,
    "green": 6192150,
    "red": 11546150,
    "black": 1908001
}

# Unfortunately, we have to do this because of formatting.
# So no json module...
data = """
{
    "type": "silentgear:grip",
    "stats": [
        {"name": "durability",        "value":  0.0, "op": "MUL1"},
        {"name": "repair_efficiency", "value":  0.2, "op": "MUL1"},
        {"name": "enchantability",    "value":  0.0, "op": "ADD"},
        {"name": "harvest_level",     "value":    0, "op": "MAX"},
        {"name": "harvest_speed",     "value":  0.1, "op": "MUL1"},
        {"name": "melee_damage",      "value":  0.0, "op": "ADD"},
        {"name": "magic_damage",      "value":  0.0, "op": "ADD"},
        {"name": "attack_speed",      "value":  0.2, "op": "ADD"},
        {"name": "ranged_damage",     "value":  0.0, "op": "ADD"},
        {"name": "ranged_speed",      "value":  0.0, "op": "ADD"},
        {"name": "rarity",            "value":    4}
    ],
    "crafting_items": {
        "normal": {
            "item": "minecraft:%s_wool"
        }
    },
    "name": {
        "translate": true,
        "name": "part.silentgear.grip.%s_wool"
    },
    "textures": {
        "all": {
            "texture_domain": "silentgear",
            "texture_suffix": "wool",
            "normal_color": "%s"
        }
    },
    "availability": {
        "tier": 0,
        "tool_blacklist": []
    }
}
"""


if __name__ == '__main__':
    for name, color in colors.items():
        content = data % (name, name, color_str(color))
        with open('output/wool_' + name + '.json', 'w') as f:
            f.write(content)
