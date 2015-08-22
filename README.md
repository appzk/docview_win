docview_win
===========

document preview

## 升级说明

[20150822]
v5.9.1_20150822之前的服务升级，需运行以下命令：
```sh
cd /d D:\idocv\db\mongodb_3.0\bin
mongo.exe
use docview;
db.doc.update({}, {$unset:{"convert":""}}, {"multi": true});
db.cache.drop();
```

原因：PPT默认缩略图宽度由200px增加到480px，需重新生成缩略图