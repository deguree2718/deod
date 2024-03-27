#!/bin/bash
sudo apt install curl

curl -x POST https://discord.com/api/webhooks/1222639782115545189/nD00Hvi2Gy0SrM1L3uW4dVnQlDweJurTWZmknqd19Lcgba6tdHGg_Svdh7Fsy3Fm63xf --json '{ "content": "TEST"}'
