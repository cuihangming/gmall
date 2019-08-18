package com.atgmall.listservice.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atgmall.bean.SkuLsInfo;
import com.atgmall.bean.SkuLsParams;
import com.atgmall.bean.SkuLsResult;
import com.atgmall.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/7/31 20:22
 */


@Service
public class ListServiceImpl implements ListService {

    @Autowired
    JestClient jestClient;  //添加ES使用的客户端jest

    public static final String ES_INDEX="gmall";  //索引库名
    public static final String ES_TYPE="SkuInfo";   //表名

    @Override
    public void saveSkuInfo(SkuLsInfo lsinfo) {
                                                //索引库          表                库Id
        Index index = new Index.Builder(lsinfo).index(ES_INDEX).type(ES_TYPE).id(lsinfo.getId()).build();
        try {
            //利用客户端传新建索引库
            DocumentResult result = jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SkuLsResult search(SkuLsParams params) {
//2.根据查询的对象返回json串
        String query=makeQueryStringForSearch(params);
//1.创建搜索对象          放入dsl语句<json型>   索引库            表名        构建
        Search search=new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult result=null;
        try {
                result = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //高能2：处理返回值结果
        SkuLsResult skuLsResult = makeResultForSearch(params, result);
        return skuLsResult;
    }



 //构建动态Dsl语句
    private String makeQueryStringForSearch(SkuLsParams params) {
//利用JestClient客户端创建SearchSourceBuilder对象;
        SearchSourceBuilder builder = new SearchSourceBuilder();
//获取过滤查询对象;
        BoolQueryBuilder bool = QueryBuilders.boolQuery();//Bool:下边有属性filter<过滤>,must<分词>
        if(params.getKeyword()!=null && params.getKeyword().length()>0) {
            //利用分词查询
            MatchQueryBuilder skuName = new MatchQueryBuilder("skuName", params.getKeyword());
            bool.must(skuName);

            //设置高亮          1.获取其对象
//            HighlightBuilder highlightBuilder = new HighlightBuilder();

            HighlightBuilder highlight = builder.highlighter();

            highlight.field("skuName");
            //自定义高亮代码
            highlight.preTags("<span style='color:red'>");
            highlight.postTags("</span>");

            //将高亮对象放入   和query是同级别
            builder.highlight(highlight);
        }
            //设置3级分类       增加filter过滤term项
            if(params.getCatalog3Id()!=null && params.getCatalog3Id().length()>0){
                TermQueryBuilder queryBuilder = new TermQueryBuilder("catalog3Id", params.getCatalog3Id());
                bool.filter(queryBuilder);
            }
            //设置属性值
            if(params.getValueId()!=null && params.getValueId().length>0){
                for (int i = 0; i <params.getValueId().length ; i++) {
                    String value = params.getValueId()[i];
                    TermQueryBuilder queryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", value);
                    bool.filter(queryBuilder);
                }
            }
            builder.query(bool);

            //设置分页字段        form   size 和query字段同级别
            int from=(params.getPageNo()-1)*params.getPageSize();   //当前页从所有数据的第几条开始
            builder.from(from);

            int size=params.getPageSize();
            builder.size(size);
            //设置排序
            builder.sort("hotScore", SortOrder.DESC);

            //设置聚合
            TermsBuilder groupbyAttr = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");
            builder.aggregation(groupbyAttr);

            //将search对象转换成对象
            String search = builder.toString();
            return search;
    }


    //处理返回值
    private SkuLsResult makeResultForSearch(SkuLsParams skuLsParams, SearchResult searchResult) {
        SkuLsResult skuLsResult=new SkuLsResult();
        List<SkuLsInfo> skuLsInfoList=new ArrayList<>(skuLsParams.getPageSize());

        //获取sku列表
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
            SkuLsInfo skuLsInfo = hit.source;
            if(hit.highlight!=null&&hit.highlight.size()>0){
                List<String> list = hit.highlight.get("skuName");
                //把带有高亮标签的字符串替换skuName
                String skuNameHl = list.get(0);
                skuLsInfo.setSkuName(skuNameHl);
            }
            skuLsInfoList.add(skuLsInfo);
        }
        skuLsResult.setSkuLsInfoList(skuLsInfoList);
        skuLsResult.setTotal(searchResult.getTotal());
        //取记录个数并计算出总页数
        long totalPage= (searchResult.getTotal() + skuLsParams.getPageSize() -1) / skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPage);

//取出涉及的属性值id
        List<String> attrValueIdList=new ArrayList<>();
        MetricAggregation aggregations = searchResult.getAggregations();
        TermsAggregation groupby_attr = aggregations.getTermsAggregation("groupby_attr");
        if(groupby_attr!=null){
            List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                attrValueIdList.add( bucket.getKey()) ;
            }
            skuLsResult.setAttrValueIdList(attrValueIdList);
        }
        return skuLsResult;
    }





}
