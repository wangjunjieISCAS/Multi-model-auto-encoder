# -*- coding: utf-8 -*-
"""
Created on Wed Aug 30 15:29:23 2017

@author: lenovo
"""

import word2vec

model = word2vec.load('data/word2vec.bin')

str = u'发现'
#str = str.decode('utf-8').encode('gbk')
print model[str]

    
#file = open ('data/input.txt')
#while 1:
#    line = file.readline()
#    if not line:
#        break
    
#    tokens = line.split(' ')
#    print tokens