package com.learning.study.spring;

/**
 * https://blog.csdn.net/a6636656/article/details/124429257
 * https://blog.csdn.net/weixin_38192427/article/details/121184221 SpringCloud之Hystrix隔离、熔断、降级
 */
public class SpringCloudLeanring {
    /**
     Spring Cloud 是一系列框架的有序集合。它利用 Spring Boot 的开发便利性巧妙地简化了分布式系统基础设施的开发，如服务发现注册、配置中心、消息总线、负载均衡、断路器、数据监控等，都可以用 Spring Boot 的开发风格
     做到一键启动和部署。Spring Cloud 并没有重复制造轮子，它只是将目前各家公司开发的比较成熟、经得起实际考验的服务框架组合起来，通过 Spring Boot 风格进行再封装屏蔽掉了复杂的配置和实现原理，最终给开发者留出了
     一套简单易懂、易部署和易维护的分布式系统开发工具包。

     1.微服务架构
        1.1 什么是微服务架构
            微服务架构就是将单体的应用程序分成多个应用程序，这多个应用程序就成为微服务，每个微服务运行在自己的进程中，并使用轻量级的机制通信。这些服务围绕业务能力来划分，并通过自动化部署机制来独立部署。这些服务可以
            使用不同的编程语言，不同数据库，以保证最低限度的集中式管理。
        1.2 为什么需要学习Spring Cloud
            首先springcloud基于spingboot的优雅简洁，可还记得我们被无数xml支配的恐惧？可还记得 springmvc，mybatis错综复杂的配置，有了spingboot，这些东西都不需要了，spingboot好处不 再赘诉，springcloud就基于
            SpringBoot把市场上优秀的服务框架组合起来，通过Spring Boot风格进行再封装屏蔽掉了复杂的配置和实现原理
            什么叫做开箱即用？即使是当年的黄金搭档dubbo+zookeeper下载配置起来也是颇费心神的！而springcloud完成这些只需要一个jar的依赖就可以了！
            springcloud大多数子模块都是直击痛点，像zuul解决的跨域，fegin解决的负载均衡，hystrix的熔断机制等等等等
        1.3 Spring Cloud 是什么
             Spring Cloud是一系列框架的有序集合。它利用Spring Boot的开发便利性巧妙地简化了分布式系统基础设施的开发，如服务发现注册、配置中心、智能路由、消息总线、负载均衡、断路器、数据监控等，都可以用Spring Boot的开发风格做到一键启动和部署。
             Spring Cloud并没有重复制造轮子，它只是将各家公司开发的比较成熟、经得起实际考验的服务框架组合起来，通过Spring Boot风格进行再封装屏蔽掉了复杂的配置和实现原理，最终给开发者留出了一套简单易懂、易部署和易维护的分布式系统开发工具包。
        1.4 SpringCloud的优缺点
             优点：
                 1.耦合度比较低。不会影响其他模块的开发。
                 2.减轻团队的成本，可以并行开发，不用关注其他人怎么开发，先关注自己的开发。
                 3.配置比较简单，基本用注解就能实现，不用使用过多的配置文件。
                 4.微服务跨平台的，可以用任何一种语言开发。
                 5.每个微服务可以有自己的独立的数据库也有用公共的数据库。
                 6.直接写后端的代码，不用关注前端怎么开发，直接写自己的后端代码即可，然后暴露接口，通过组件进行服务通信。
             缺点：
                 1.部署比较麻烦，给运维工程师带来一定的麻烦。
                 2.针对数据的管理比麻烦，因为微服务可以每个微服务使用一个数据库。
                 3.系统集成测试比较麻烦
                 4.性能的监控比较麻烦。【最好开发一个大屏监控系统】
            总的来说优点大过于缺点，目前看来Spring Cloud是一套非常完善的分布式框架，目前很多企业开始用微服务、Spring Cloud的优势是显而易见的。因此对于想研究微服务架构的同学来说，学习Spring Cloud是一个不错的选择。
        1.5 SpringBoot和SpringCloud的区别？
             SpringBoot专注于快速方便的开发单个个体微服务。
             SpringCloud是关注全局的微服务协调整理治理框架，它将SpringBoot开发的一个个单体微服务整合并管理起来，
             为各个微服务之间提供，配置管理、服务发现、断路器、路由、微代理、事件总线、全局锁、决策竞选、分布式会话等等集成服务
             SpringBoot可以离开SpringCloud独立使用开发项目， 但是SpringCloud离不开SpringBoot ，属于依赖的关系
             SpringBoot专注于快速、方便的开发单个微服务个体，SpringCloud关注全局的服务治理框架。
        1.6 SpringCloud由什么组成
             这就有很多了，我讲几个开发中最重要的
             Spring Cloud Eureka：服务注册与发现
             Spring Cloud Zuul：服务网关
             Spring Cloud Ribbon：客户端负载均衡
             Spring Cloud Feign：声明性的Web服务客户端
             Spring Cloud Hystrix：断路器
             Spring Cloud Config：分布式统一配置管理
             等20几个框架，开源一直在更新
        1.7 Spring Cloud 和dubbo区别?
            （1）服务调用方式：dubbo是RPC springcloud Rest Api
            （2）注册中心：dubbo 是zookeeper springcloud是eureka，也可以是zookeeper
            （3）服务网关，dubbo本身没有实现，只能通过其他第三方技术整合，springcloud有Zuul路由网关，作为路由服务器，进行消费者的请求分发,springcloud支持断路器，与git完美集成配置文件支持版本控制，事物总线实现配置文件的更新
                与服务自动装配等等一系列的微服务架构要素。

     2.Eureka
        2.1 服务注册和发现是什么意思？Spring Cloud 如何实现？
            当我们开始一个项目时, 我们通常在属性文件中进行所有的配置, 随着越来越多的服务开发和部署, 添加和修改这些属性变得更加复杂, 有些服务可能会下架, 而某些位置可能会发生变化, 手动更改属性可能会产生问题, Eureka服务注册和
            发现可以在这种情况下提供帮助,由于所有服务都在Eureka服务起上注册并通过调用Eureka服务器完成查找, 因此无需处理服务地点的任何更改和处理
        2.2 什么是Eureka
            Eureka作为SpringCloud的服务注册功能服务器，他是服务注册中心，系统中的其他服务使用Eureka的客户端将其连接到Eureka Service中，并且保持心跳，这样工作人员可以通过EurekaService来监控各个微服务是否运行正常。
        2.3 Eureka怎么实现高可用
            集群吧，注册多台Eureka，然后把SpringCloud服务互相注册，客户端从Eureka获取信息时，按照Eureka的顺序来访问。
        2.4 什么是Eureka的自我保护模式
            默认情况下，如果Eureka Service在一定时间内没有接收到某个微服务的心跳，Eureka Service会进入自我保护模式，在该模式下Eureka Service会保护服务注册表中的信息，不在删除注册表中的数据，当网络故障恢复后，
            Eureka Servic 节点会自动退出自我保护模式
        2.5 DiscoveryClient的作用
            可以从注册中心中根据服务别名获取注册的服务器信息。
        2.6 Eureka和ZooKeeper都可以提供服务注册与发现的功能,请说说两个的区别
             ZooKeeper中的节点服务挂了就要选举 在选举期间注册服务瘫痪,虽然服务最终会恢复,但是选举期间不可用的， 选举就是改微服务做了集群，必须有一台主其他的都是从
             Eureka各个节点是平等关系,服务器挂了没关系，只要有一台Eureka就可以保证服务可用，数据都是最新的。 如果查询到的数据并不是最新的，就是因为Eureka的自我保护模式导致的
             Eureka本质上是一个工程,而ZooKeeper只是一个进程
             Eureka可以很好的应对因网络故障导致部分节点失去联系的情况,而不会像ZooKeeper 一样使得整个注册系统瘫痪
             ZooKeeper保证的是CP，Eureka保证的是AP
             CAP： C：一致性>Consistency; 取舍：(强一致性、单调一致性、会话一致性、最终一致性、弱一致性) A：可用性>Availability; P：分区容错性>Partition tolerance;

     3.Zuul
        3.1 什么是网关?
            网关相当于一个网络服务架构的入口，所有网络请求必须通过网关转发到具体的服务。
        3.2 网关的作用是什么
            统一管理微服务请求，权限控制、负载均衡、路由转发、监控、安全控制黑名单和白名单等
        3.3 什么是Spring Cloud Zuul（服务网关）
             Zuul是对SpringCloud提供的成熟对的路由方案，他会根据请求的路径不同，网关会定位到指定的微服务，并代理请求到不同的微服务接口，他对外隐蔽了微服务的真正接口地址。三个重要概念:动态路由表，
            路由定位，反向代理:
                (1)动态路由表:Zuu支持Eureka路由，手动配苦路由，这俩种都支持自动更新
                (2)路由定位:根据请求路径，Zuul有自己的一套定位服务规则以及路由表达式匹配
                (3)反向代理:客户端请求到路由网关，网关受理之后，在对目标发送请求，拿到响应之后在给客户端
            它可以和Eureka, Ribbon, Hystrix等组件配合使用
            Zuul的应用场景:
                对外暴露，权限校验，服务聚合，日志审计等
        3.4 网关与过滤器有什么区别
            网关是对所有服务的请求进行分析过滤，过滤器是对单个服务而言。
        3.5 常用网关框架有那些？
            Nginx、Zuul、Gateway
        3.6 Zuul与Nginx有什么区别？
            Zuul是java语言实现的，主要为java服务提供网关服务，尤其在微服务架构中可以更加灵活的对网关进行操作。Nginx是使用C语言实现，性能高于Zuul，但是实现自定义操作需要熟悉lua语言，对程序员要求较高，可以使用Nginx做Zuul集群。
        3.7 zuul和gateway的区别
             内部实现不同：
                gateway对比zuul多依赖了spring-webflux，在spring的支持下，功能更强大，内部实现了限流、负载均衡等，扩展性也更强，但同时也限制了仅适合于Spring Cloud套件zuul则可以扩展至其他微服务框架中。
             是否支持异步：
                zuul仅支持同步gateway支持异步。理论上gateway则更适合于提高系统吞吐量（但不一定能有更好的性能），最终性能还需要通过严密的压测来决定。
             框架设计的角度：
                gateway具有更好的扩展性，并且其已经发布了2.0.0的RELESE版本，稳定性也是非常好的。
        3.8 如何设计一套API接口
            考虑到API接口的分类可以将API接口分为开发API接口和内网API接口，内网API接口用于局域网，为内部服务器提供服务。开放API接口用于对外部合作单位提供接口调用，需要遵循Oauth2.0权限认证协议。同时还需要考虑安全性、幂等性等问题。
        3.9 ZuulFilter常用有那些方法
             Run()：过滤器的具体业务逻辑
             shouldFilter()：判断过滤器是否有效
             fifilterOrder()：过滤器执行顺序
             fifilterType()：过滤器拦截位置
        3.10 如何实现动态Zuul网关路由转发
            通过path配置拦截请求，通过ServiceId到配置中心获取转发的服务列表，Zuul内部使用Ribbon实现本地负载均衡和转发
        3.11 Zuul网关如何搭建集群
            使用Nginx的upstream设置Zuul服务集群，通过location拦截请求并转发到upstream，默认使用轮询机制对Zuul集群发送请求

     4.Ribbon
        4.1 负载平衡的意义什么？
            简单来说:先将集群，集群就是把一个的事情交给多个人去做，假如要做1000个产品给一个人做要10天，我叫10个人做就是一天，这就是集群，负载均衡的话就是用来控制集群，他把做的最多的人让他慢慢做休息会，把做的最少的人让他加量让他做多点。
            在计算中，负载平衡可以改善跨计算机，计算机集群，网络链接，中央处理单元或磁盘驱动器等多种计算资源的工作负载分布。负载平衡旨在优化资源使用，最大化吞吐量，最小化响应时间并避免任何单一资源的过载，使用多个组件进行负载平衡而不是
            单个组件可能会通过冗余来提高可靠性和可用性。负载平衡通常涉及专用软件或硬件，例如多层交换机或域名系统服务器进程。
        4.2 Ribbon是什么？
             Ribbon是Netflix发布的开源项目，主要功能是提供客户端的软件负载均衡算法
             Ribbon客户端组件提供一系列完善的配置项，如连接超时，重试等。简单的说，就是在配置文件中列出后面所有的机器，Ribbon会自动的帮助你基于某种规则(如简单轮询，随即连接等)去连接这些机器。
            我们也很容易使用Ribbon实现自定义的负载均衡算法。(有点类似Nginx)
        4.3 Nginx与Ribbon的区别
             Nginx是反向代理同时可以实现负载均衡，nginx拦截客户端请求采用负载均衡策略根据upstream
             配置进行转发，相当于请求通过nginx服务器进行转发。Ribbon是客户端负载均衡，从注册中心读取目标服务器信息，然后客户端采用轮询策略对服务直接访问，全程在客户端操作。
        4.4 Ribbon底层实现原理
            Ribbon使用discoveryClient从注册中心读取目标服务信息，对同一接口请求进行计数，使用%取余算法获取目标服务集群索引，返回获取到的目标服务信息。
            @LoadBalanced注解的作用
                开启客户端负载均衡。
            如:
                @LoadBalanced
                @Autowired
                private List<RestTemplate> restTemplates = Collections.emptyList();
            也就是，所有加了@LoadBalanced注解的RestTemplate，会被注入到这个地方，在这个地方，实质上是进行了RestTemplate的自定义配置。

     5.Hystrix
        5.1 什么是断路器
            当一个服务调用另一个服务由于网络原因或自身原因出现问题, 调用者就会等待被调用者的响应, 当更多的服务请求到这些资源导致更多的请求等待, 发生连锁效应(雪崩效应)
            断路器有三种状态:
                打开状态: 一段时间内达到一定的次数无法调用, 并且多次监测美欧恢复的迹象, 断路器完全打开, 那么下次请求就不会请求该服务
                半开状态: 短时间内, 有恢复迹象, 断路器会将部分请求发给该服务, 正常调用时, 断路器关闭
                关闭状态: 当服务一直处于正常状态, 能正常调用
        5.2 什么是Hystrix?
            在分布式系统, 我们一定会依赖各种服务, 那么这些个服务一定会出现失败的情况, 就会导致雪崩, Hystrix就是这样的一个工具, 防雪崩利器, 它具有服务降级, 服务熔断, 服务隔离, 监控等
            一些防止雪崩的技术
            Hystrix有四种防雪崩方式:
                (1)服务降级: 接口调用事变就调用本地的方法返回一个空
                (2)服务熔断: 接口调用失败就会进入调用接口提前定义好的一个熔断的方法, 返回错误信息
                (3)服务隔离: 隔离服务之间相互影响
                (4)服务监控: 在服务发生调用时, 会将每秒请求数, 成功请求数等运行指标记录下来
        5.3 谈谈服务雪崩效应
            雪崩效应是在大型互联网项目中，当某个服务发生宕机时，调用这个服务的其他服务也会发生宕机，大型项目的微服务之间的调用是互通的，这样就会将服务的不可用逐步扩大到各个其他服务中，从而使整个项目的服务
            宕机崩溃发生雪崩效应的原因有以下几点:
                (1)单个服务的代码存在bug.
                (2)请求访问量激增导致服务发生崩溃(如大型商城的枪红包，秒杀功能).
                (3)服务器的硬件故障也会导致部分服务不可用
        5.4 在微服务中, 如何保护服务?
            一般使用使用Hystrix框架，实现服务隔离来避免出现服务的雪崩效应，从而达到保护服务的效果。当微服务中，高并发的数据库访问量导致服务线程阻塞，使单个服务宕机，服务的不可用会蔓延到其他服务，
            引起整体服务灾难性后果，使用服务降级能有效为不同的服务分配资源一旦服务不可用则返回友好提示，不占用其他服务资源，从而避免单个服务崩溃引发整体服务的不可用
        5.5 服务雪崩效应产生的原因
            因为Tomcat默认情况下只有一个线程池来维护客户端发送的所有的请求，这时候某一接口在某一时刻被大量访问就会占据tomcat线程池中的所有线程，其他请求处于等待状态，无法连接到服务接口。
        5.6 谈谈服务降级、熔断、服务隔离
            服务降级:
                当客户端请求服务器端的时候，防止客户端一直等待，不会处理业务逻辑代码，直接返回一个友好的提示给客户端。
            服务熔断:
                是在服务降级的基础上更直接的一种保护方式，当在一个统计时间范围内的请求失败数量达到设定值(requestVolumeThreshold)或当前的请求错误率达到设定的错误率阈值
                (errorThresholdPercentage)时开启断路，之后的请求直接走fallback方法，在设定时间(sleepWindowlnMilliseconds)后尝试恢复。
            服务隔离:
                就是Hystrix为隔离的服务开启一个独立的线程池，这样在高并发的情况下不会影响其他服务。服务隔离有线程池和信号量两种实现方式，一般使用线程池方式。
        5.7 服务降级、熔断、服务隔离的使用
            #从springcloud Dalston版本开始，Feign的Hystrix支持默认关闭，需要手动设置开启
                feign.hystrix.enabled=true
            #第一次启动项目时，请求接口查询数据库有点耗时，会进入降级策略，所以将hystrix的超时时间设置为3s，默认是1s，这是全局设置
                hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=3000
            5.7.1 服务降级的实现(以下服务下线或服务超时都可测)
                (1)方式一
                    使用 feign 的注解 @FeignClient 的属性 fallback 指定的降级回退方法
                     @FeignClient(name = "eureka-client-producer", fallback = UserConsumerFeignFallback.class)
                     public interface UserConsumerFeign {
                         @GetMapping(path = "/user/selectUserById")
                         ResultVo selectUserById(@RequestParam(name = "id") Integer id);
                     }
                (2)方式二
                     使用 hystrix 提供的注解 @HystrixCommand 的属性 fallbackMethod 来指定降级回退方法
                     @Slf4j
                     @Service
                     public class UserConsumerServiceImpl implements UserConsumerService {
                             @Autowired
                             private UserConsumerFeign userConsumerFeign;

                            @HystrixCommand(fallbackMethod = "queryOneByIdFallback")
                            @Override
                            public ResultVo queryOneById(Integer id) {
                                ResultVo resultVo = userConsumerFeign.selectUserById(id);
                                log.info("resultVo为：" + resultVo.toString());
                                log.info("调用服务提供方的端口为：" + resultVo.getPort());
                                return resultVo;
                            }

                            public ResultVo queryOneByIdFallback(Integer id) {
                                ResultVo resultVo = new ResultVo();
                                resultVo.setId(id);
                                resultVo.setUsername("恭喜你已进入UserConsumerServiceImpl类所在的服务降级区域");
                                resultVo.setNickname("恭喜你已进入UserConsumerServiceImpl类所在的服务降级区域");
                                return resultVo;
                            }
                    }
                (3)方式三
                     每一个方法独立配置降级的话会造成代码冗余，加大了工作量。此时可以使用注解 @DefaultProperties 的 defaultFallback 属性来指定类的全局降级回退方法
                     @RestController
                     @Slf4j
                     @DefaultProperties(defaultFallback = "payment_Global_FallackMethod")
                     public class PaymentController {

                             @Resource
                             private PaymentHystrixService paymentService;

                             @GetMapping("/consumer/payment/hystrix/ok/{id}")
                             public String paymentInfo_OK(@PathVariable("id") Integer id) {
                                 String result = paymentService.paymentInfo_OK(id);
                                 log.info("*******result:" + result);
                                 return result;
                             }

                             @GetMapping("/consumer/payment/hystrix/timeout/{id}")
                             @HystrixCommand(fallbackMethod = "paymentTimeOutFallbackMethod", commandProperties = {
                             @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1500")  //3秒钟以内就是正常的业务逻辑
                             })
                             public String paymentInfo_TimeOut(@PathVariable("id") Integer id) {
                                 String result = paymentService.paymentInfo_TimeOut(id);
                                 return result;
                             }

                             //兜底方法
                             public String paymentTimeOutFallbackMethod(@PathVariable("id") Integer id) {
                                return "我是消费者80，对付支付系统繁忙请10秒钟后再试或者自己运行出错请检查自己,(┬＿┬)";
                             }

                              //全局fallback处理方法
                            public String payment_Global_FallackMethod(@PathVariable("id") Integer id) {
                                return "我是消费者80，对付支付系统繁忙请10秒钟后再试或者自己运行出错请检查自己,(┬＿┬)";
                            }
                        }
            5.7.2 服务熔断的实现
                 熔断与降级的区别:
                     (1)服务熔断的核心是断路器（跳闸），没有断路器（配置）的熔断那就不是熔断了
                     (2)服务熔断也会触发服务降级回退方法的
                     (3)服务熔断的配置：回退，兜底方法 + 断路器配置，二者缺一不可
                     (4)服务降级的配置：回退，兜底方法


     */
}
































