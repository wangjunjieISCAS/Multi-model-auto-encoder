对于一个新项目，
1. 运行java程序MultiModelTestData，生成word token.txt文件，并且对图片进行重新编号
2. 运行python程序testDataPreparation，生成work token.txt文件的word embedding形式

3. 运行MultiModelSDA程序，生成测试报告的high-level features
【MY】

4. 运行python程序performanceEvaluation程序，进行性能评价
【不需要运行duplicateDetection，performanceEvaluation会调用duplicateDetection】



如果是只用wordEmbedding的baseline
不运行3，只运行1,2,4



