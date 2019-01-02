#!/bin/sh

mvn clean package
#cp target/AuthExtension.jar $HOME/Documents/smartfox-sfs2x/Game/AuthExtension.jar
#cp target/AuthExtension.jar $HOME/Documents/smartfox-sfs2x/Auth/AuthExtension.jar
cp target/AuthExtension.jar $HOME/Documents/smartfox-extensions/__lib__/AuthExtension.jar

