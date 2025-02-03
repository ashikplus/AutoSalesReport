package com.ashik.AutoSalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface SalesRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByDateBetween(LocalDate start, LocalDate end);
}
