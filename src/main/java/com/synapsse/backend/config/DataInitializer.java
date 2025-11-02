package com.synapsse.backend.config;

import com.synapsse.backend.entity.Product;
import com.synapsse.backend.entity.Role;
import com.synapsse.backend.entity.User;
import com.synapsse.backend.repository.ProductRepository;
import com.synapsse.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@synapsse.com")) {
                User admin = new User();
                admin.setEmail("admin@synapsse.com");
                admin.setPassword(passwordEncoder.encode("Admin1234"));
                admin.setFirstName("Admin");
                admin.setLastName("Synapsse");
                admin.setPhone("+54 11 0000-0000");
                admin.setAddress("Av. Siempre Viva 123");
                admin.setCity("Buenos Aires");
                admin.setProvince("CABA");
                admin.setPostalCode("1000");
                admin.setRoles(EnumSet.of(Role.ADMIN, Role.CUSTOMER));
                userRepository.save(admin);
            }
        };
    }

    @Bean
    public CommandLineRunner seedProducts(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                Product[] products = {
                    createProduct("Crema Restauradora", "Crema facial con células madre vegetales de arroz para hidratación profunda y luminosidad.", 34.00, "/producto1.png", Set.of("Crema", "Hidratación"), 50),
                    createProduct("Contorno de Ojos", "Efecto antiarrugas y reducción de bolsas. Textura ligera y de rápida absorción.", 22.00, "/producto2.png", Set.of("Contorno", "Anti-edad"), 30),
                    createProduct("Sérum de Ácido Hialurónico", "Concentrado potente para rellenar y aportar elasticidad a la piel.", 28.00, "/producto3.png", Set.of("Sérum", "Hidratación"), 40),
                    createProduct("Bálsamo Labial Nutritivo", "Protege y repara los labios con ingredientes naturales.", 12.00, "/producto4.png", Set.of("Labial", "Nutritivo"), 100),
                    createProduct("Tónico Equilibrante", "Tónico suave que regula el pH y prepara la piel para absorber mejor los activos.", 18.00, "/producto5.png", Set.of("Tónico"), 60),
                    createProduct("Mascarilla de Arcilla", "Mascarilla purificante que limpia los poros y aporta textura equilibrada al cutis.", 20.00, "/producto6.png", Set.of("Mascarilla", "Purificante"), 45),
                    createProduct("Protector Solar Facial SPF50", "Protección alta, acabado ligero y no graso para uso diario.", 26.00, "/producto7.png", Set.of("Protector", "Solar"), 70),
                    createProduct("Exfoliante Suave", "Microexfoliante con partículas naturales para renovar la piel sin irritación.", 15.00, "/producto8.png", Set.of("Exfoliante"), 55),
                    createProduct("Aceite de Noche Reparador", "Fórmula reparadora nocturna que nutre y regenera durante el sueño.", 30.00, "/producto9.png", Set.of("Aceite", "Reparador"), 35),
                    createProduct("Crema de Manos Nutritiva", "Crema ligera de rápida absorción para manos suaves y protegidas.", 10.00, "/producto10.png", Set.of("Crema", "Manos"), 80)
                };
                
                for (Product product : products) {
                    productRepository.save(product);
                }
            }
        };
    }

    private Product createProduct(String name, String description, double price, String imageUrl, Set<String> categories, int stock) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(BigDecimal.valueOf(price));
        product.setImageUrl(imageUrl);
        product.setCategories(categories);
        product.setStock(stock);
        return product;
    }
}
