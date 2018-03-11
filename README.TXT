
Facebook Grabber

@author: Kido
@email: everlastxgb@gmail.com
@version: V0.3 (201803020407)

（一）、使用前须知：
1.使用前，请确保自己的电脑已经安装了JDK。若未安装JDK，可以上 http://www.oracle.com/technetwork/java/javase/downloads/jdk9-downloads-3848520.html 下载安装。
2.确认JDK正确安装完毕后，双击目录底下的”Start”启动程序。

（二）、操作指南：
1.请先输入Facebook的帐号、密码进行登录。
2.”Group Id / URL” 中可以输入你想要爬取的群组的唯一标示或者群组的主页地址，多个群组用英文分号隔开。
3.“Limit” 用于限制爬取群组中的成员数最多为多少个，不填写或者填写小于等于0则代表无限制。
4.“Clear Log” 清除输出日志。
5.”Clear Cache” 清除内存缓存。
6.“Open File Folder” 打开导出的excel所在的文件夹。
7.”Grab it!” 开始爬取群组信息。
8.”Stop it!” 中断当前执行任务。
9.“Open it!” 用浏览器打开群组。

（三）、导出数据字段说明：
NickName: 用户昵称
UserId: 用户ID
UserName: 用户唯一标示名
Gender: 性别
Hometown: 故乡
Location: 所在地
Role: 在群组中的角色。0:普通成员，1:管理员
JoinInfo: 成员加入群组的信息
ProfileUrl: 个人主页地址

（四）、注意事项：
1.使用本程序，需要翻墙。
2.若要指定爬取的内容为中文或者英文，可登录网页版Facebook进入“设置”将语言选为中文或者英文。


＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
［Release Note］

V0.3(201803020407)
1.用户不展示性别的情况下，通过 she her him he 他 她 等字眼推导。
2.NickName、Hometown、Location分多行显示。
————————————

V0.2(201802281439)
1.修复一些bug
————————————
V0.1（201802251900）
1.Facebook模拟登录，爬取群组信息和成员信息，并导出生成到excel。

