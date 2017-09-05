#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Aug 28 15:29:28 2017

@author: limingyang
"""

import numpy as np
import tensorflow as tf

#权值初始化函数，将所有的权重初始化到一个正态分布 fan_in是输入节点的数量 fan_out是输出节点的数量
def xavier_init(fan_in, fan_out, constant = 1):
    low = -constant * np.sqrt(6.0 / (fan_in+fan_out))
    high = constant * np.sqrt(6.0 / (fan_in+fan_out))
    results = tf.random_uniform((fan_in, fan_out), minval = low, maxval = high, dtype = tf.float32)
    return results

class MMAutoEncoder(object):
    
    def __init__(self, 
               n_input = [3,3],
               n_hidden = [ [2,2], [2], [2,2] ],
               transfer_function = tf.nn.softplus,
               optimizer = tf.train.AdamOptimizer(learning_rate=0.001),
               scale = 0.001):
        #网络结构
        self.n_input = n_input#整体的输入数据
        self.n_hidden = n_hidden
        self.resource_count = len(n_input)#resource的个数
        #激活函数
        self.transfer = transfer_function
        #学习率
        self.scale = tf.placeholder(tf.float32)
        self.training_scale = scale
        #网络权重
        self.weights = self._initialize_weights()
        
        #网络的输入
        self.x1 = tf.placeholder(tf.float32, [None, self.n_input[0]])
        self.x2 = tf.placeholder(tf.float32, [None, self.n_input[1]])
        self.x = tf.concat([self.x1 , self.x2],1)#输入向量
        
        ###编码阶段###
        #resource1的hidden1
        resource1_hidden1 = self.transfer( 
                tf.add( tf.matmul(self.x1, self.weights['W11']), self.weights['b11'] ) )
        #resource2的hidden1
        resource2_hidden1 = self.transfer( 
                tf.add( tf.matmul(self.x2, self.weights['W12']), self.weights['b12'] ) )
        #resource1和resource2的hidden1输出合并为一个向量，作为下一层的输入
        joint_hidden1 = tf.concat([resource1_hidden1 , resource2_hidden1],1)
        #联合特征
        self.joint_feature =  self.transfer( 
                tf.add( tf.matmul(joint_hidden1, self.weights['W2']), self.weights['b2'] ) )
        
        ###解码阶段###
        resource1_hidden3 = self.transfer( 
                tf.add( tf.matmul(self.joint_feature, self.weights['W31']), self.weights['b31'] ) )
        resource2_hidden3 = self.transfer( 
                tf.add( tf.matmul(self.joint_feature, self.weights['W32']), self.weights['b32'] ) )
        self.x1_o = tf.add( tf.matmul(resource1_hidden3, self.weights['W41']), self.weights['b41'] ) 
        self.x2_o = tf.add( tf.matmul(resource2_hidden3, self.weights['W42']), self.weights['b42'] ) 
        self.x_o = tf.concat([self.x1_o , self.x2_o],1)#输出向量
        
        #使用平方误差作为cost
        self.cost = 0.5 * tf.reduce_sum(
                    tf.pow( tf.subtract(self.x_o,self.x), 2.0  )   )
        #+tf.reduce_sum(
        #           tf.pow( tf.subtract(self.x2_o,self.x2), 2.0  )   ))
        self.optimizer = optimizer.minimize(self.cost)
        
        init = tf.global_variables_initializer()
        self.session = tf.Session()
        self.session.run(init)
        print("begin to run session")
    
    #权值初始化
    def _initialize_weights(self):
        all_weights = dict()
        all_weights['W11'] = tf.Variable( xavier_init(self.n_input[0], self.n_hidden[0][0] ),dtype=tf.float32 )
        all_weights['b11'] = tf.Variable( tf.zeros([self.n_hidden[0][0] ],dtype=tf.float32 ) )
        all_weights['W12'] = tf.Variable( xavier_init(self.n_input[1], self.n_hidden[0][1] ),dtype=tf.float32 )
        all_weights['b12'] = tf.Variable( tf.zeros([self.n_hidden[0][1]],dtype=tf.float32 ) )
        all_weights['W2'] = tf.Variable( tf.zeros([self.n_hidden[0][0]+self.n_hidden[0][1],
                                                  self.n_hidden[1][0] ],dtype=tf.float32 ) )
        all_weights['b2'] = tf.Variable( tf.zeros([self.n_hidden[1][0]],dtype=tf.float32 ) )
        
        all_weights['W31'] = tf.Variable( tf.zeros([self.n_hidden[1][0],
                                                  self.n_hidden[2][0] ],dtype=tf.float32 ) )
        all_weights['b31'] = tf.Variable( tf.zeros([self.n_hidden[2][0]],dtype=tf.float32 ) )
        
        all_weights['W32'] = tf.Variable( tf.zeros([self.n_hidden[1][0],
                                                  self.n_hidden[2][1] ],dtype=tf.float32 ) )
        all_weights['b32'] = tf.Variable( tf.zeros([self.n_hidden[2][1] ],dtype=tf.float32 ) )
        
        all_weights['W41'] = tf.Variable( tf.zeros([self.n_hidden[2][0],
                                                  self.n_input[0] ],dtype=tf.float32 ) )
        all_weights['b41'] = tf.Variable( tf.zeros([self.n_input[0] ],dtype=tf.float32 ) )
        
        all_weights['W42'] = tf.Variable( tf.zeros([self.n_hidden[2][1],
                                                  self.n_input[1] ],dtype=tf.float32 ) )
        all_weights['b42'] = tf.Variable( tf.zeros([self.n_input[1] ],dtype=tf.float32 ) )
        return all_weights
    
    #训练函数
    def partial_fit(self, X1, X2):
        #session = tf.Session()
        cost, opt = self.session.run( (self.cost, self.optimizer),
                                  feed_dict = {self.x1: X1, 
                                               self.x2: X2,
                                               self.scale: self.training_scale })
        return cost
    
    #返回每一次训练的损失，但不会触发训练操作
    def calc_total_cost(self, X1, X2):
        cost = self.session.run( (self.cost),
                                  feed_dict = {self.x1: X1, 
                                               self.x2: X2,
                                               self.scale: self.training_scale })
        return cost
    
    #获取联合特征表示
    def transform(self, X1, X2):
        joint_feature = self.session.run( (self.joint_feature),
                                  feed_dict = {self.x1: X1, 
                                               self.x2: X2,
                                               self.scale: self.training_scale })
        return joint_feature
    
    #获取最后输出
    def reconstruct(self, X1, X2):
        output = self.session.run( (self.x_o),
                                  feed_dict = {self.x1: X1, 
                                               self.x2: X2,
                                               self.scale: self.training_scale })
        return output
    

#从训练数据中生成batch 用于训练模型
def get_random_block_from_data(data, batch_size):
    start_index = np.random.randint(0, len(data) - batch_size)
    return data[start_index:(start_index+batch_size)]
    

if __name__=="__main__":   
    #训练的轮数
    training_epochs = 7000
    display_step = 1
    batch_size = 128
    sample_size=2000

    #rng = np.random.RandomState(500)
    #train_data = rng.uniform(0,1,(sample_size,200))

    autoencoder = MMAutoEncoder()
    
    batch_x1=[[1,0,1],[1,1,1],[0,0,1]]
    batch_x2=[[1,1,0],[0,0,0],[1,1,1]]
    
    for epoch in range(training_epochs):
        cost = autoencoder.partial_fit(batch_x1, batch_x2)
        print("Epoch:",'%04d'%(epoch+1),"cost=","{:.9f}".format(cost))

    #for epoch in range(training_epochs):
    #    avg_cost = 0.0
    #    total_batch = int(sample_size/batch_size)
    #    for i in range(total_batch):
    #        batch_x = get_random_block_from_data(train_data,batch_size)
    #        batch_x1 = [temp[0:100]for temp in batch_x]
    #        batch_x2 = [temp[100:200]for temp in batch_x]
    #        cost = autoencoder.partial_fit(batch_x1, batch_x2)
    #        avg_cost += cost/sample_size*batch_size
        
    #    if epoch%display_step ==0:
    #        print("Epoch:",'%04d'%(epoch+1),"cost=","{:.9f}".format(avg_cost))
            
    print("#########predict############")
    rng = np.random.RandomState(100)
    test_data = rng.uniform(0,1,(1,200))
    #print(test_data)
    #batch_x1 = [temp[0:100]for temp in test_data]
    #batch_x1 = [1,1,1,1,1,1,1,1,1,1]
    #batch_x2 = [temp[100:200]for temp in test_data]
    x1=[[0,0,1]]
    x2=[[1,1,1]]
    results=autoencoder.reconstruct(x1,x2)
    transform=autoencoder.transform(x1,x2)
    print(results)
    print(transform)
    #print(autoencoder.reconstruct(source1,source2).shape())
    #avg_cost = 0.0
    #cost = autoencoder.partial_fit(source1, source2)
    #        avg_cost += cost/200*batch_size
        
    #    if epoch%display_step ==0:
    #       print("cost=","{:.9f}".format(avg_cost))    
        
    
        
        