# -*- coding: utf-8 -*-
"""
Created on Tue Jul 25 17:10:16 2017

@author: lenovo
"""
#load_ext autoreload
#autoreload 2
# ipython

import word2vec
import codecs
from sentence2vec import Sentence2Vec
import numpy as np

class Word:
    def __init__(self, text, vector):
        self.text = text
        self.vector = vector


# a sentence, a list of words
class Sentence:
    def __init__(self, word_list, word_frequency):
        self.word_list = word_list
        self.word_frequency = word_frequency

    # return the length of a sentence
    def len(self):
        return len(self.word_list)
    
class Document2Vec:
    def train_model(self):
        word2vec.word2vec('data/train.txt', 'data/word2vec.bin', size=200, verbose=True)
        
    def get_model(self):
        return word2vec.load('data/word2vec.bin')
  
    def predict_word2vec(self, word, model):
        if word in model.vocab:
            return model[word]
        else:
            return None
    
    def obtain_sentence_list (self):
        sentence_list = []
        model = self.get_model()
        
        file = codecs.open('data/multiTrainText.txt','r', 'utf-8')
        content = file.readlines()
        file.close()

        token_num = 0
        unfind_token_num = 0
        for line in content:
            line = line.replace('\r\n', '')
            print line
            tokens = line.split(' ')
            
            # find the no duplicate word in the sentence, and 
            no_dup_tokens = []
            tf_tokens = []
            for word in tokens:
                #print "word -1 :" + word
                if word in no_dup_tokens:
                    index = no_dup_tokens.index(word )
                    tf_tokens[index] += 1
                else:
                    no_dup_tokens.append ( word)
                    tf_tokens.append (1)
            
            sentence_words = []
            for word in no_dup_tokens:
                #print "word: " + word
                #new_word = word.encode('utf-8', 'ignore').decode('utf-8')
                vector = self.predict_word2vec(word, model)
                if ( not (vector is None) ) :                    
                    word_vector = Word( word, vector )
                    sentence_words.append (word_vector)
                    token_num += 1
                else:
                    unfind_token_num += 1
            
            sentence = Sentence (sentence_words, tf_tokens)
            sentence_list.append (sentence)
        
        print token_num
        print unfind_token_num
        
        return sentence_list
    
    def predict_sentence2vec(self, sentence_list, embedding_size):
        sen2vec = Sentence2Vec()
        sentence_vecs = sen2vec.sentence_to_vec(sentence_list, embedding_size)
        return sentence_vecs   
    
    def predict_text2vec_obtain_text_source (self):        
        sentence_list = self.obtain_sentence_list( )
        sentence_vecs, zero_len_list = self.predict_sentence2vec(sentence_list, 200)
        np.savetxt('data/textInput.txt', sentence_vecs)
        np.save('data/zeroLenList.npy', zero_len_list)
        return zero_len_list
    

doc2vec = Document2Vec()   
zero_len_list = doc2vec.predict_text2vec_obtain_text_source()
temp = np.load('data/zeroLenList.npy')
print temp

#doc2vec = Document2Vec()           
#sentence_list = doc2vec.obtain_sentence_list( )
#sentence_vecs, zero_len_list = doc2vec.predict_sentence2vec(sentence_list, 200)
#np.savetxt('data/textInput.txt', sentence_vecs)
#print zero_len_list
#print len(sentence_vecs)

