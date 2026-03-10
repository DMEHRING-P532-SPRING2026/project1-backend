package iu.devinmehringer.project1.mapper;

import iu.devinmehringer.project1.dto.user.UserResponse;
import iu.devinmehringer.project1.model.user.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper implements Mapper<User, UserResponse>{

    private final StockHoldingMapper stockHoldingMapper;

    public UserMapper(StockHoldingMapper stockHoldingMapper) {
        this.stockHoldingMapper = stockHoldingMapper;
    }

    @Override
    public UserResponse toDTO(User entity) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(entity.getId());
        userResponse.setBalance(entity.getBalance());
        userResponse.setTotalPortfolioValue(entity.getPortfolioValue());
        userResponse.setStockHoldingResponses(
                entity.getHoldings().stream()
                        .map(stockHoldingMapper::toDTO)
                        .collect(Collectors.toList())
        );
        return userResponse;
    }
}
