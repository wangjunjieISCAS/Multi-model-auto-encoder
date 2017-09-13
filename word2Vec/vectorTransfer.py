# -*- coding: utf-8 -*-
"""
Created on Wed Aug 30 15:29:23 2017

@author: lenovo
"""

import numpy as np

class VectorTransfer:
    def transfer_vector (self, embedding_size):
        text_vectors = []
        
        empty_vector = []
        for i in range(embedding_size):
            empty_vector.append (0.00)
        
        zero_list = np.load('data/zeroLenListTest.npy')
        print zero_list
        
        text = np.loadtxt('data/testText.txt')
        
        total_size = len(text) + len(zero_list)
        index_text = 0
        for i in range(total_size):
            if i in zero_list:
                text_vectors.append ( empty_vector )
            else:
                text_vectors.append (text[index_text] ) 
                index_text = index_text +1
        
        np.savetxt('data/testTextTotal.txt', text_vectors)

vectorTool = VectorTransfer()
embedding_size = 100
vectorTool.transfer_vector( embedding_size )