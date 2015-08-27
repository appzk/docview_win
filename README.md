docview_win
===========

document preview

## 操作说明

### 批量删除文件
需要两步操作： 
1. 删除文件： 
数据目录是按日期区分，可以在目录d:\idocv\data\...下找到最旧日期的文件夹直接删除； 
2. 删除数据库记录： 
命令行> cd /d d:\idocv\db\mongodb-2.4.8\bin 
mongo.exe 
use docview 
db.doc.remove({ "ctime" : { "$gt" : "2014-08-21 00:00:00", "$lt" : "2014-08-22 00:00:00" } }) 
上面的日期范围就是要删除的日期范围记录 



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