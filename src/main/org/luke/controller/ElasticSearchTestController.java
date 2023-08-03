package org.luke.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.luke.entity.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/es")
public class ElasticSearchTestController {

    @Resource
    private ElasticsearchClient elasticsearchClient;
    private FieldValue.Builder v;

    @GetMapping("/index")
    public ResponseEntity<Object> getIndexData(String indexName) throws IOException {
        SearchResponse<Product> search = elasticsearchClient.search(
                s -> s
                    .index(indexName)
                    .query(
                        q -> q
                            .term(t -> t
                                    .field("pdu")
                                    .value(v -> v.stringValue("bk"))
                            )
                    ),
                Product.class);

        List<Hit<Product>> hits = search.hits().hits();
        List<Product> list = new ArrayList<>();
        for (Hit<Product> hit : hits) {
            System.out.println(hit.source());
            list.add(hit.source());
        }
        return ResponseEntity.ok(list);
    }

    @PostMapping("/index")
    public ResponseEntity<Object> insertIndex(String indexName) throws IOException {
        Product product = new Product("bk-1", "City bike", 123.0);

        IndexResponse response = elasticsearchClient.index(i -> i
                .index("products")
                .id(product.getPdu())
                .document(product)
        );
        return ResponseEntity.ok(response);
    }
}
