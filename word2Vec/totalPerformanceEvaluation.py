# -*- coding: utf-8 -*-
"""
Created on Fri Sep 29 11:29:16 2017

@author: wang
"""

from performanceEvaluation import PerformanceEvaluation
import csv

class TotalPerformanceEvaluation:
    
    def obtainPerformance ( self, type, reportVectorFile, predictLabelFile, trueLabelFile ):
        evaluation = PerformanceEvaluation()
        evaluation.generate_prediction_labels ( reportVectorFile , predictLabelFile )

        result = evaluation.compute_performance (  trueLabelFile, predictLabelFile )
        print result
        
        headers = ['type', 'recal-1', 'recall-3', 'recall-5', 'recall-10', 'MAP', 'MRR']
        value = ( type, result['recall-1'], result['recall-3'], result['recall-5'], result['recall-10'], result['MAP'], result['MRR'])
        
        value_list = []
        value_list.append ( value )
        
        with open( 'data/total_performance.csv', 'a+') as f:
            writer = csv.writer(f)
            writer.writerow ( headers )
            writer.writerow ( value )
            
        f.close()
        
   
dupDetection =TotalPerformanceEvaluation();
#dupDetection.obtainPerformance ( 'multi', "data/report_vector.txt", "data/predict_labels.txt", "data/true_labels.txt");

#dupDetection.obtainPerformance ( 'text', "data/testTextInputTotal.txt", "data/predict_labels_text.txt", "data/true_labels.txt" );

dupDetection.obtainPerformance ( 'text',  "data/imageVectorTestOut.txt", "data/predict_labels_image.txt", "data/true_labels.txt" );