#!/bin/sh

mvn clean package
#cp target/DemoExtension.jar $HOME/Documents/smartfox-sfs2x/Game/DemoExtension.jar
cp target/DemoExtension.jar $HOME/Documents/smartfox-extensions/__lib__/DemoExtension.jar

