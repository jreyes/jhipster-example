package jhipster.web.rest;

import com.codahale.metrics.annotation.Timed;
import jhipster.domain.Category;
import jhipster.repository.CategoryRepository;
import jhipster.repository.search.CategorySearchRepository;
import jhipster.web.rest.util.HeaderUtil;
import jhipster.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Category.
 */
@RestController
@RequestMapping("/api")
public class CategoryResource {

    private final Logger log = LoggerFactory.getLogger(CategoryResource.class);

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private CategorySearchRepository categorySearchRepository;

    /**
     * POST  /categorys -> Create a new category.
     */
    @RequestMapping(value = "/categorys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Category> createCategory(@RequestBody Category category) throws URISyntaxException {
        log.debug("REST request to save Category : {}", category);
        if (category.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new category cannot already have an ID").body(null);
        }
        Category result = categoryRepository.save(category);
        categorySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/categorys/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("category", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /categorys -> Updates an existing category.
     */
    @RequestMapping(value = "/categorys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Category> updateCategory(@RequestBody Category category) throws URISyntaxException {
        log.debug("REST request to update Category : {}", category);
        if (category.getId() == null) {
            return createCategory(category);
        }
        Category result = categoryRepository.save(category);
        categorySearchRepository.save(category);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("category", category.getId().toString()))
                .body(result);
    }

    /**
     * GET  /categorys -> get all the categorys.
     */
    @RequestMapping(value = "/categorys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Category>> getAllCategorys(Pageable pageable)
        throws URISyntaxException {
        Page<Category> page = categoryRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/categorys");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /categorys/:id -> get the "id" category.
     */
    @RequestMapping(value = "/categorys/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        log.debug("REST request to get Category : {}", id);
        return Optional.ofNullable(categoryRepository.findOne(id))
            .map(category -> new ResponseEntity<>(
                category,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /categorys/:id -> delete the "id" category.
     */
    @RequestMapping(value = "/categorys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.debug("REST request to delete Category : {}", id);
        categoryRepository.delete(id);
        categorySearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("category", id.toString())).build();
    }

    /**
     * SEARCH  /_search/categorys/:query -> search for the category corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/categorys/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Category> searchCategorys(@PathVariable String query) {
        return StreamSupport
            .stream(categorySearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
