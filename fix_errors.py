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
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/magic/ExplosionMagicCloudEmitter.kt",
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/meteorite/MeteoriteTailEmitter.kt",
    "/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic/particles/emitters/TailEmitter.kt"
]

for f in files:
    with open(f, 'r') as file:
        content = file.read()
    
    new_content = re.sub(r'@Transient\n(\s*val \w+ by lazy \{)', r'\1', content)
    
    if new_content != content:
        with open(f, 'w') as file:
            file.write(new_content)
        print(f"Fixed {f}")
