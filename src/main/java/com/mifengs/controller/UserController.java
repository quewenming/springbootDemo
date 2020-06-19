package com.mifengs.controller;

import com.mifengs.domain.User;
import com.mifengs.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public Flux<User> getUserAll(){
       return userRepository.findAll();
    }

    @GetMapping(value = "/stream/userAll",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> streamGetUserAll(){
        return userRepository.findAll();
    }

    @PostMapping("/")
    public Mono<User> createUser(@RequestBody User user){
        //spring data jpa里面，新增和修改都是save，有id时为修改，id为空是新增，根据实际情况是否置空id
        user.setId(null);
        return this.userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable("id") String id){
        return this.userRepository.findById(id).flatMap(user -> this.userRepository.delete(user).
                then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))).
                defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable("id") String id,@RequestBody User user){
        return this.userRepository.findById(id).flatMap(u -> {
            u.setName(user.getName());
            u.setAge(user.getAge());
            return this.userRepository.save(u);
        })
        .map(u -> new ResponseEntity<User>(HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> findUserById(@PathVariable("id") String id){
        return this.userRepository.findById(id)
                .map(u -> new ResponseEntity<User>(HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/age/{start}/{end}")
    public Flux<User> findUserByAge(@PathVariable("start") int start,@PathVariable("end") int end){
        return this.userRepository.findByAgeBetween(start,end);
    }

    @GetMapping(value = "/stream/age/{start}/{end}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> streamFindUserByAge(@PathVariable("start") int start,@PathVariable("end") int end){
        return this.userRepository.findByAgeBetween(start,end);
    }

    @GetMapping("/old")
    public Flux<User> oldUser(){
        return this.userRepository.oldUser();
    }

    @GetMapping(value = "/stream/old",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> streamOldUser(){
        return this.userRepository.oldUser();
    }
}
