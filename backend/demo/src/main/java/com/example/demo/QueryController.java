package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/queries")
@CrossOrigin(origins = "*")
public class QueryController {

    @Autowired
    private UserQueryRepository userQueryRepository;

    // 1. USER SENDS MESSAGE
    @PostMapping("/send")
    public UserQuery sendQuery(@RequestBody UserQuery query) {
        return userQueryRepository.save(query);
    }

    // 2. ADMIN FETCHES ALL
    @GetMapping("/all")
    public List<UserQuery> getAllQueries() {
        return userQueryRepository.findAll();
    }

    // 3. ADMIN REPLIES
    @PutMapping("/reply/{id}")
    public UserQuery reply(@PathVariable String id, @RequestBody Map<String, String> body) {
        return userQueryRepository.findById(id).map(q -> {
            q.setAdminReply(body.get("reply"));
            q.setResolved(true);
            return userQueryRepository.save(q);
        }).orElse(null);
    }

    // 4. USER FETCHES THEIR REPLIES
    @GetMapping("/my-queries/{email}")
    public List<UserQuery> getMyQueries(@PathVariable String email) {
        return userQueryRepository.findByUserEmail(email);
    }
}
