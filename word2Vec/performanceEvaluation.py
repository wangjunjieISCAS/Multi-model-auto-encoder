# -*- coding: utf-8 -*-
"""
Created on Mon Sep 18 17:29:41 2017

@author: wang
"""
import csv
from duplicateDetection import DuplicationDetection


class PerformanceEvaluation:

    # the file which store the true label information should have a column named 'TRUE'
    def obtain_true_labels (self, input_file, output_file):
        tag_list = []
        with open( input_file ) as f:
            f_csv = csv.DictReader(f)
            for row in f_csv:
                #print row['TRUE'].decode("gbk").encode("utf-8")
                tag_list.append ( row['TRUE'].decode("gbk").encode("utf-8") )
                
        print len(tag_list)
        
        tag_list_size = len(tag_list)
        with open( output_file, 'w') as f:
            for i in range(tag_list_size):
                temp = tag_list[i]
                f.write ( str(i) + ":" )
                if not temp:
                    f.write ( "" + "\n" )
                    continue
                
                output = ""
                for j in range(tag_list_size):
                    if i == j:
                        continue;
                    value = tag_list[j]
                    if ( temp == value ):
                        output += str(j) + " "
                            
                f.write ( output + "\n")
        
        f.close
        
        
    def generate_prediction_labels ( self, input_file, output_file):
        dup_detect = DuplicationDetection()
        vector_dict = dup_detect.load_report_vectors( input_file )
        
        dup_detect.detect_duplicate_report_for_project ( vector_dict, output_file )
        
    
    def read_results (self, file_name ):
        file = open( file_name ) 
        
        result_dict = {}
        for line in file:
            temp = line.split ( ":" ) 
            
            index = int(temp[0])
            #print temp[1]
            
            if ( len(temp) > 1 and len(temp[1]) > 1 ):
                values = temp[1].split(" ")
                result_dict[index] = values;
            else:
                result_dict[index] = []
                
        return result_dict
    
    # compute the recall-5, recall-10, MAP and MRR values
    def compute_performance ( self, true_file, predict_file ):
        true_label = self.read_results ( true_file )
        predict_label = self.read_results ( predict_file )
        
        recall_1_list = []
        recall_2_list = []
        recall_3_list = []
        recall_4_list= []
        average_precision_list = []
        first_index_list= []
        valid_num = 0
        
        for index in predict_label.keys():
            predicts = predict_label[index]
            trues = true_label[index]
            
            # for reports which have no duplicate report in true_set, just ignore it
            if len(trues) == 0 :
                print "the size of trues is zero"
                continue
            
            recall = 0.0
            for i in range(len(predicts)):
                dup = predicts[i]
                if dup in trues:
                    recall = 1.0
                    break
                if i >= 1:
                    break;
            
            recall_1_list.append (recall)
            
            recall = 0.0
            for i in range(len(predicts)):
                dup = predicts[i]
                if dup in trues:
                    recall = 1.0
                    break
                if i >= 3:
                    break;
            recall_2_list.append (recall)
            
            recall = 0.0
            for i in range(len(predicts)):
                dup = predicts[i]
                if dup in trues:
                    recall = 1.0
                    break
                if i >= 5:
                    break;
                
            recall_3_list.append (recall)
            
            recall = 0.0
            for i in range(len(predicts)):
                dup = predicts[i]
                if dup in trues:
                    recall = 1.0
                    break
                if i >= 10:
                    break;
                
            recall_4_list.append (recall)
            
            precision_total = 0.0
            for i in range(len(trues)):
                count = 0
                for j in range (i):
                    dup = predicts[j]
                    if dup in trues:
                        count += 1
                precision_total += (1.0*count) / (1.0*(i+1))
            
            precision_total = precision_total / len(trues)
            
            average_precision_list.append ( precision_total )
            
            first_index = len(predicts)*2
            for i in range(len(predicts)):
                dup = predicts[i]
                if dup in trues:
                    first_index = i+1
                    break
            
            if first_index == 0 :
                first_index = 10000
            first_index_list.append ( 1.0 / first_index )
            
            valid_num += 1
        
        if ( len(recall_1_list) != len(recall_2_list) and len(recall_1_list) != len(average_precision_list) and  len(recall_1_list) != len(first_index_list) ):
            print "warong list size"
        
        result = {}
        recall_1_total = 0.0
        recall_2_total = 0.0
        recall_3_total = 0.0
        recall_4_total = 0.0
        MAPTotal = 0.0
        MRRTotal = 0.0
        for i in range(len(recall_1_list)):
            recall_1_total += recall_1_list[i]
            recall_2_total += recall_2_list[i]
            recall_3_total += recall_3_list[i]
            recall_4_total += recall_4_list[i]
            
            MAPTotal += average_precision_list[i]
            MRRTotal += first_index_list[i]
        
        
        result['recall-1'] = recall_1_total / valid_num
        result['recall-3'] = recall_2_total / valid_num
        result["recall-5"] = recall_3_total / valid_num
        result["recall-10"] = recall_4_total / valid_num
        result["MAP"] = MAPTotal / valid_num
        result["MRR"] = MRRTotal / valid_num
        
        print result["recall-1"]
        print result["recall-3"] 
        print result["recall-5"]
        print result["recall-10"] 
        print result["MAP"]
        print result["MRR"] 
        
        return result

               
evaluation = PerformanceEvaluation()
#evaluation.obtain_true_labels( "data/true_in.csv", "data/true_labels.txt")
#evaluation.generate_prediction_labels ( "data/report_vector.txt", "data/predict_labels.txt" )

#evaluation.compute_performance ( "data/true_labels.txt", "data/predict_labels.txt"  )