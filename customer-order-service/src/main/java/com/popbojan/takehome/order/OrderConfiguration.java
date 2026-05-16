package com.popbojan.takehome.order;

import com.popbojan.takehome.order.domain.CreateOrderUseCase;
import com.popbojan.takehome.order.domain.GetOrderUseCase;
import com.popbojan.takehome.order.domain.ListOrdersUseCase;
import com.popbojan.takehome.order.domain.PatchOrderUseCase;
import com.popbojan.takehome.order.domain.activity.CreateOrderActivity;
import com.popbojan.takehome.order.domain.activity.GetOrderActivity;
import com.popbojan.takehome.order.domain.activity.ListOrdersActivity;
import com.popbojan.takehome.order.domain.activity.UpdateOrderActivity;
import com.popbojan.takehome.order.domain.activity.ValidateOrderActivity;
import com.popbojan.takehome.order.domain.activity.ValidateProductOfferingsActivity;
import com.popbojan.takehome.order.domain.activity.ValidateStateTransitionActivity;
import com.popbojan.takehome.order.domain.port.CustomerOrderPort;
import com.popbojan.takehome.order.domain.port.IdempotencyPort;
import com.popbojan.takehome.order.domain.port.ProductCatalogPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfiguration {

    @Bean
    GetOrderActivity getOrderActivity(CustomerOrderPort customerOrderPort) {
        return new GetOrderActivity(customerOrderPort);
    }

    @Bean
    GetOrderUseCase getOrderUseCase(GetOrderActivity getOrderActivity) {
        return new GetOrderUseCase(getOrderActivity);
    }

    @Bean
    ListOrdersUseCase listOrdersUseCase(CustomerOrderPort customerOrderPort) {
        return new ListOrdersUseCase(new ListOrdersActivity(customerOrderPort));
    }

    @Bean
    CreateOrderUseCase createOrderUseCase(
            CustomerOrderPort customerOrderPort,
            ProductCatalogPort productCatalogPort,
            GetOrderActivity getOrderActivity,
            IdempotencyPort idempotencyPort
    ) {
        return new CreateOrderUseCase(
                new CreateOrderActivity(customerOrderPort),
                new ValidateOrderActivity(),
                new ValidateProductOfferingsActivity(productCatalogPort),
                getOrderActivity,
                idempotencyPort
        );
    }

    @Bean
    PatchOrderUseCase patchOrderUseCase(
            CustomerOrderPort customerOrderPort,
            ProductCatalogPort productCatalogPort,
            GetOrderActivity getOrderActivity
    ) {
        return new PatchOrderUseCase(
                getOrderActivity,
                new UpdateOrderActivity(customerOrderPort),
                new ValidateOrderActivity(),
                new ValidateProductOfferingsActivity(productCatalogPort),
                new ValidateStateTransitionActivity()
        );
    }
}
