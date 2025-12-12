#!/bin/bash

# 可以在这里修改版本号
FRP_VERSION="0.28.2"
TAG_NAME="v${FRP_VERSION}"

URL="https://github.com/fatedier/frp/archive/refs/tags/${TAG_NAME}.tar.gz"
echo "Downloading frpc sources ${TAG_NAME} from ${URL}"

curl -L -o "src.tgz" $URL
rm -rf frp-*
tar -xzvf src.tgz
rm -f src.tgz
