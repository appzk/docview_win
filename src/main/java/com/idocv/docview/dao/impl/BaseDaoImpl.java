package com.idocv.docview.dao.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.BaseDao;
import com.mongodb.DB;

@Repository("baseDao")
public class BaseDaoImpl implements BaseDao {
	
	public static final String _ID = "_id"; // 主键，mid+ruid,required
	public static final String UID = "uid"; // mid作者id,required
	public static final String RUID = "ruid"; // 参与者id,,required
	public static final String CTIME = "ctime"; // 邮件创建时间，如果后加入的，为加入时间,required
	public static final String JTIME = "jtime"; // 邮件参与者加入时间,required
	public static final String UTIME = "utime"; // 更新时间,required
	public static final String RTIME = "rtime"; // ruid的阅读时间,required
	public static final String MID = "mid"; // 邮件id,仅限根邮件,required
	public static final String MCOUNT = "mcount"; // 该邮件的所有回复数量，不包含自己,required
	public static final String NEWRC = "newrc"; // 新回复数
	public static final String STATUS = "status"; // 该mid的状态,required
	public static final String META = "meta"; // 元数据扩展,optional
	public static final String TAG = "tag"; // 标签扩展，optional,仅限ruid给该mid打的自定义标签,optional
	public static final String DTIME = "dtime"; // 删除时间,optional
	public static final String TITLE = "title";
	public static final String CONTENT = "content";
	public static final String CONTENT_EXT = "contentext"; // 扩展内容，如链接解析内容等
	public static final String ATTACHMENTS = "attachments";
	public static final String FROM = "from";
	public static final String TYPE = "type";
	public static final String PMID = "pmid"; // 父mid，如果该mid为根邮件，pmid为自己，required
	public static final String RID = "rid"; // 资源id，required
	public static final String RMID = "rmid"; // 最新回复mid，创建此记录rmid=mid
	public static final String NAME = "name"; // tag的名称
	public static final String INDEX = "index"; // tag的排序
	public static final String COLOR = "color"; // tag的的颜色
	public static final String OMID = "omid"; // 被转发邮件的mid
	public static final String AUID = "auid"; // 添加人的uid
	public static final String LOC = "loc"; // 坐标
	public static final String ADDRESS = "address"; // 地址
	public static final String DFLAG = "dflag"; // draft flag
	public static final String CITY = "city"; // draft flag
	public static final String LIKE = "like"; // 赞
	public static final String SHARE = "share"; // 分享
	public static final String TOPIC_TYPE = "topicType"; // 话题类型
	public static final String LAST_REPLY_LIST = "lastReplyList"; // 最后n条回复列表
	public static final String FOLLOWER_COUNT = "fcount"; // 参与者人数
	public static final String SIZE = "size"; // 附件大小
	public static final String DESC = "desc"; // 附件描述
	public static final String PTID = "ptid"; // parent topic id
	public static final String TID = "tid"; // topic id

	@Resource
	protected DB db;

}
