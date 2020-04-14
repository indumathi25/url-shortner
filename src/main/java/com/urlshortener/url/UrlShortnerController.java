package com.urlshortener.url;

import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/cache")
public class UrlShortnerController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    UrlRepository urlRepository;

    @PostMapping("/writeback/createUrl")
    public String writeBackCreateUrl(@RequestBody UrlRequest urlRequest) throws ExecutionException, InterruptedException {
        UrlValidator urlValidator = new UrlValidator(new String[]{"https","http"});
        String url = urlRequest.getUrl();
        System.out.println(urlValidator.isValid(url));
        if(urlValidator.isValid(url)){
            //MurmurHash is an algorithm used to create an unique identifier from a particular text.
            String hashString = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
            stringRedisTemplate.opsForValue().set(hashString, url);

            ExecutorService threadPool = Executors.newCachedThreadPool();
            ListeningExecutorService service = MoreExecutors.listeningDecorator(threadPool);
            ListenableFuture<Long> guavaFuture = (ListenableFuture<Long>) service.submit(()-> {
                        UrlEntity entity = new UrlEntity(hashString, url);
                        urlRepository.save(entity);
                    });
            guavaFuture.get();
            return hashString;
        }
        return "Invalid Url";
    }

    @PostMapping("/writethrough/createUrl")
    public String writeThroughCreateUrl(@RequestBody UrlRequest urlRequest){
        UrlValidator urlValidator = new UrlValidator(new String[]{"https","http"});
        String url = urlRequest.getUrl();
        System.out.println(urlValidator.isValid(url));
        if(urlValidator.isValid(url)){
            //MurmurHash is an algorithm used to create an unique identifier from a particular text.

            //Write through - will save data in both redis and DB.

            String hashString = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
            stringRedisTemplate.opsForValue().set(hashString, url);
            //
            UrlEntity entity = new UrlEntity(hashString, url);
            urlRepository.save(entity);
            //
            return hashString;
        }
        return "Invalid Url";
    }

    @PostMapping("/writearound/createUrl")
    public String writeAroundCreateUrl(@RequestBody UrlRequest urlRequest){
        UrlValidator urlValidator = new UrlValidator(new String[]{"https","http"});
        String url = urlRequest.getUrl();
        System.out.println(urlValidator.isValid(url));
        if(urlValidator.isValid(url)){
            //MurmurHash is an algorithm used to create an unique identifier from a particular text.
            String hashString = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();

            //
            UrlEntity entity = new UrlEntity(hashString, url);
            urlRepository.save(entity);
            //
            return hashString;
        }
        return "Invalid Url";
    }

    @GetMapping("/writearound/getUrl/{hashUrl}")
    @Cacheable(value = "post-single", key = "#hashUrl", unless = "#result.shares < 500")
    public String getUrl(@PathVariable String hashUrl){
        try{
            if(hashUrl != "") {
                String actualUrl = stringRedisTemplate.opsForValue().get(hashUrl);
                if(actualUrl == "") {
                    List<UrlEntity> entityList = urlRepository.findByHashUrl(hashUrl);
                    if (entityList.size() > 0) {
                        UrlEntity url = entityList.get(0);
                        //
                        String hashString = Hashing.murmur3_32().hashString(url.getHashUrl(), StandardCharsets.UTF_8).toString();
                        stringRedisTemplate.opsForValue().set(hashString, url.getHashUrl());
                        return url.getUrl();
                    }
                } else {
                    return actualUrl;
                }
            } else {
                return "Enter a valid hash code";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "Invalid hash URL";
    }
}
