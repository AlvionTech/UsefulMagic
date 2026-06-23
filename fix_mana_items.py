import os
import re

directory = '/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/items/consumer/'

def fix_file(path):
    with open(path, 'r') as f:
        content = f.read()

    # Match `user.maxMana += ...` or `user.manaAbsorptionRate += ...`
    # and move them inside the `if (!world.isClientSide) { ... }` block.
    # Also change `return super.use(world, user, hand)` to `return InteractionResultHolder.sidedSuccess(user.getItemInHand(hand), world.isClientSide)`
    
    if 'return super.use(world, user, hand)' in content:
        # Move the mutations inside the block
        content = re.sub(
            r'if \(!world\.isClientSide\) \{([\s\S]*?)\}\s*(user\.maxMana \+= \d+)\s*(stack\.count -= 1)',
            r'if (!world.isClientSide) {\1\n            \2\n            \3\n        }',
            content
        )
        content = re.sub(
            r'if \(!world\.isClientSide\) \{([\s\S]*?)\}\s*(user\.manaAbsorptionRate \+= \d+)\s*(stack\.count -= 1)',
            r'if (!world.isClientSide) {\1\n            \2\n            \3\n        }',
            content
        )
        
        # Replace the return statement
        content = content.replace(
            'return super.use(world, user, hand)',
            'return InteractionResultHolder.sidedSuccess(user.getItemInHand(hand), world.isClientSide)'
        )
        
        with open(path, 'w') as f:
            f.write(content)
        print(f"Fixed {path}")

for filename in os.listdir(directory):
    if filename.endswith(".kt"):
        fix_file(os.path.join(directory, filename))

