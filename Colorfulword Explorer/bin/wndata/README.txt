这里的文件除了DataManager.java和Color.java之外都来自于一个开源的Wordnet浏览器(URCSWordNetBrowser,http://www.cs.rochester.edu/research/cisd/wordnet/),这个开源浏览器的代码我放到项目主页(http://code.google.com/p/colorfulword/)的download里面了,如果有问题可以看看他的源代码.用他的代码主要是因为他已经把各个元素的结构搭好了.WordNetReader可以从数据文件中读数据,不过我们具体要用的时候可能要修改一下.

那个WordNetReader我们本来应该是要自己实现的,如果用它的代码,理论上根据开源协议我们就必须说明我们的代码来自它们.这样也没关系,不过要用那个WordNetReader的话还是把他的逻辑搞清楚,确保如果自己实现也能写得出.

这些文件都得看一看,因为是把一部分移过来,如果不能用还可能要改.


在几个文件里我都写了一点LZYNOTE,是我猜测可能要修改或者注意的地方,把所有文件看过一遍之后可以看看.

-------------以上 by liuzhiyou

