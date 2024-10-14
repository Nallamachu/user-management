package com.msrts.hostel.repository;

import com.msrts.hostel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "from User u where u.email=?1")
    User findByEmail(String email);

    @Query(value = "from User u where u.mobile like %?1%")
    User findByMobile(String mobile);

    @Query(value = "from User u where u.id in (?1)")
    Set<User> findByIdIn(List<Integer> ids);

    @Query(value = """
            select distinct pu.user_id from partner_users pu where pu.group_id=3 and pu.user_id in (102,53)
            """, nativeQuery = true)
    List<Integer> findIdsNotInPartnerUsersForGivenGroupId(List<Integer> ids, Long groupId);

    @Query(value = "delete from partner_users WHERE group_id = ?1", nativeQuery = true)
    Integer deletePartnerUsersByGroupId(Long groupId);

    @Query(value = "from User u where u.username=?1")
    User findByUsername(String username);

    @Query(value = "from User u where u.username=?1 or u.mobile=?2")
    User findByUsernameOrMobile(String username, String mobile);

    @Query(value = "from User u where u.referralCode=?1")
    User findByReferralCode(String referredByCode);

    @Query(value = "select * from user u where u.id in (select distinct pu.user_id from partner_users pu where pu.group_id=?1)", nativeQuery = true)
    List<User> findAllUsersByPartnerGroupId(Long groupId);
}
