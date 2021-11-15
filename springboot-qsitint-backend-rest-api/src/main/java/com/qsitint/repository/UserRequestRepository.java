package com.qsitint.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qsitint.domain.UserRequest;

@Repository
public interface UserRequestRepository extends PagingAndSortingRepository<UserRequest, Long> {

    @Query("FROM UserRequest b WHERE b.description LIKE %:searchText% OR b.requestType LIKE %:searchText% ORDER BY b.id ASC")
    Page<UserRequest> findAllUserRequests(Pageable pageable, @Param("searchText") String searchText);
}
