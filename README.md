# Fabric-AccessToken-Importer (FATI)
A simple application to copy your Minecraft access token inside your Fabric development projects for Intellij IDEA and Eclipse.

#### How does FATI works?
1) It imports all tokens of all accounts from the minecraft folder
2) It selects the token of your preferred account
3) It searches for your launch configuration files of you fabric projects
4) It inserts the token into the launch configuration files

#### Initial setup: 
1. run FATI once, and it will generate an empty config file for you.
2. modify the config file to satisfy your needs (see example config file below)

#### How to run FATI: 
`java -jar fabric-accesstoken-importer-1.0.0-SNAPSHOT.jar`

#### How to build FATI:
`mvn clean install`

#### Example config.json for Unix file system:
```
{
  "minecraftFolder": "/home/daniel/.minecraft",
  "workspaceFolder": "/home/daniel/git",
  "minecraftUsername": "d4n1el89",
  "uuid": "a90ff7fd-59f0-489f-9992-8766f5068ad3",
  "foldersToIgnore": [
    "fabric-example-mod",
    "folder2"
  ]
}
```







