package com.it.common.nlp;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;

import java.util.List;

public class NLPAi {

    public static void main(String[] args) {

        //SegmentationAlgorithm 的可选类型为：
        //正向最大匹配算法：MaximumMatching
        //逆向最大匹配算法：ReverseMaximumMatching
        //正向最小匹配算法：MinimumMatching
        //逆向最小匹配算法：ReverseMinimumMatching
        //双向最大匹配算法：BidirectionalMaximumMatching
        //双向最小匹配算法：BidirectionalMinimumMatching
        //双向最大最小匹配算法：BidirectionalMaximumMinimumMatching
        //全切分算法：FullSegmentation
        //最少分词算法：MinimalWordCount
        //最大Ngram分值算法：MaxNgramScore

        //https://my.oschina.net/apdplat/blog/228619


        List<Word> wordsList = WordSegmenter.seg("孙本,是创互地图应用级产品开发平台的作者");
        System.out.println(wordsList);

        Long startTime=System.currentTimeMillis();
        List<Word> wordsList2 = WordSegmenter.segWithStopWords("你是谁");
        System.out.println(wordsList2);

        long stopTime = System.currentTimeMillis();
        System.out.println((stopTime-startTime)*1d/1000d);
    }

}
