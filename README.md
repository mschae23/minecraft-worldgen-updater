## Minecraft World Generation Updater
This tool can update configured features to a newer Minecraft version.

### Usage

#### 1.18-pre1 or newer
```java -jar minecraft-worldgen-updater-2.0.0.jar update features <configured input> <placed input> <configured output> <placed output>```

These should be references to the folders that contain the feature files.

#### From 21w44a or older
```java -jar minecraft-worldgen-updater-2.0.0.jar update features <input folder> <output folder> --legacy```

Input should be configured features, and the output will be placed features.
