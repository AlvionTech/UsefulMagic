import os
import re

files = [
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/entity/dragon/emitter/skills/DragonBreathEmitter.kt",
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/entity/eye/MagicEyeHurtEmitter.kt",
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/entity/eye/MagicThornEmitter.kt",
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/LightningParticleEmitters.kt",
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/magic/BlockFragmentEmitters.kt",
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/magic/MeteoriteExplosionEmitter.kt",
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/magic/StarryHugeBarrageExplosionEmitter.kt",
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/magic/StarryMeteoriteSplitEmitter.kt",
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/magic/BarrageTailEmitter.kt",
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/magic/ExplosionMagicCloudEmitter.kt",
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/meteorite/MeteoriteTailEmitter.kt",
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/TailEmitter.kt"
]

def wrap_with_lazy(content):
    # We find 'val name = ParticleCommandQueue()'
    # And we need to find the end of the expression.
    # Since it's a chain of method calls, it typically ends before 'override fun' or 'fun ' or 'val '
    # A simple way: find the start, then find the next line that matches '^\s*(override |fun |val |var |init |@)' or ends the class '}'
    
    lines = content.split('\n')
    new_lines = []
    i = 0
    in_lazy = False
    lazy_indent = ""
    
    while i < len(lines):
        line = lines[i]
        
        # Match 'val <name> = ParticleCommandQueue()'
        match = re.search(r'^(\s*)val\s+(\w+)\s*=\s*ParticleCommandQueue\(\)', line)
        if match and "private fun" not in line and "return ParticleCommandQueue" not in line:
            indent = match.group(1)
            name = match.group(2)
            new_lines.append(indent + f"@Transient")
            new_lines.append(indent + f"val {name} by lazy {{")
            new_lines.append(indent + "    ParticleCommandQueue()")
            in_lazy = True
            lazy_indent = indent
            i += 1
            continue
            
        if in_lazy:
            # Check if this line is the start of a new declaration
            if re.match(r'^\s*(override |fun |val |var |init |@|//|/\*|$|\})', line) and not line.strip().startswith('.'):
                if line.strip() != "":
                    # Close the lazy block
                    new_lines.append(lazy_indent + "}")
                    in_lazy = False
            elif line.strip() == "":
                # empty lines inside lazy block are fine, but might be the end. We just keep adding them
                pass
            else:
                # Indent the chained calls
                pass
                
        if in_lazy:
            new_lines.append("    " + line)
        else:
            new_lines.append(line)
            
        i += 1
        
    if in_lazy:
        new_lines.append(lazy_indent + "}")
        
    return '\n'.join(new_lines)

for f in files:
    with open(f, 'r') as file:
        content = file.read()
    
    # Check for build functions returning queues like in MeteoriteExplosionEmitter
    # private fun buildSp1Queue(): ParticleCommandQueue {
    #     return ParticleCommandQueue() ...
    if "private fun build" in content:
        print(f"Skipping or manual fix needed for {f}")
        pass

    new_content = wrap_with_lazy(content)
    
    # Also wrap doTick
    # find override fun doTick() { ... }
    # Since we can't easily parse braces, let's just do a simple replacement if it's a single line inside doTick
    # or just replace "override fun doTick() {" with "override fun doTick() {\n        if (world?.isClientSide != true) return\n"
    if "override fun doTick() {" in new_content:
        new_content = new_content.replace("override fun doTick() {", "override fun doTick() {\n        if (world?.isClientSide != true) return")
    elif "override fun doTick() \n    {" in new_content:
        new_content = new_content.replace("override fun doTick() \n    {", "override fun doTick() {\n        if (world?.isClientSide != true) return")

    if new_content != content:
        with open(f, 'w') as file:
            file.write(new_content)
        print(f"Fixed {f}")
    
