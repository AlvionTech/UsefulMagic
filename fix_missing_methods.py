import os
import glob

directory = '/home/lopyhupis/Documents/Projects/Projects/UsefulMagic/common/src/main/kotlin/cn/coostack/usefulmagic'

# 1. Add addPreTickActionPost to Action classes
action_files = [
    'animate/CompositionAction.kt',
    'animate/DisplayAction.kt',
    'animate/EmitterAction.kt',
    'animate/RenderAction.kt',
    'animate/TickableAction.kt'
]

for rel in action_files:
    path = os.path.join(directory, rel)
    if os.path.exists(path):
        with open(path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Determine the class name from the file
        cls_name = rel.split('/')[-1].replace('.kt', '')
        
        if 'fun addPreTickActionPost' not in content:
            # We want to add it right after addPreTickAction
            if cls_name == 'CompositionAction':
                sig = 'override fun addPreTickActionPost(action: CompositionAction<T>.() -> Unit): CompositionAction<T> { return this }'
            elif cls_name == 'DisplayAction':
                sig = 'override fun addPreTickActionPost(action: DisplayAction<T>.() -> Unit): DisplayAction<T> { return this }'
            elif cls_name == 'EmitterAction':
                sig = 'override fun addPreTickActionPost(action: EmitterAction<T>.() -> Unit): EmitterAction<T> { return this }'
            elif cls_name == 'RenderAction':
                sig = 'override fun addPreTickActionPost(action: RenderAction<T>.() -> Unit): RenderAction<T> { return this }'
            elif cls_name == 'TickableAction':
                sig = 'override fun addPreTickActionPost(action: TickableAction.() -> Unit): TickableAction { return this }'
            else:
                continue

            content = content.replace('override fun addPreTickAction(', sig + '\n\n    override fun addPreTickAction(')
            
            with open(path, 'w', encoding='utf-8') as f:
                f.write(content)

# 2. Fix setTextureSheet strings in specific files
setTextureSheetFiles = [
    'entity/custom/MagicBookEntity.kt',
    'formation/CrystalFormation.kt',
    'items/weapon/magic/HealthMagic.kt',
    'items/weapon/magic/LightningMagic.kt'
]

for rel in setTextureSheetFiles:
    path = os.path.join(directory, rel)
    if os.path.exists(path):
        with open(path, 'r', encoding='utf-8') as f:
            content = f.read()
            
        content = content.replace('setTextureSheet(TextureSheetsEnum.PARTICLE_SHEET_TRANSLUCENT)', 'setTextureSheet("PARTICLE_SHEET_TRANSLUCENT")')
        content = content.replace('setTextureSheet(TextureSheetsEnum.PARTICLE_SHEET_OPAQUE)', 'setTextureSheet("PARTICLE_SHEET_OPAQUE")')
        content = content.replace('setTextureSheet(ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT)', 'setTextureSheet("PARTICLE_SHEET_TRANSLUCENT")')
        content = content.replace('setTextureSheet(ParticleRenderType.PARTICLE_SHEET_OPAQUE)', 'setTextureSheet("PARTICLE_SHEET_OPAQUE")')
        
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)

