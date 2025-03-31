# Category & Search Controllers - Low Level Design

## Category Operations Flow

```mermaid
sequenceDiagram
    participant Client
    participant CategoryController
    participant AdminCategoryController
    participant CategoryService
    participant SearchService
    participant Repository
    
    %% Get Category Tree
    Client->>CategoryController: GET /api/categories/tree
    CategoryController->>CategoryService: getCategoryTree()
    CategoryService->>Repository: findByParentIsNullAndActiveTrue()
    CategoryService-->>CategoryController: List<CategoryDto>
    CategoryController-->>Client: 200 OK
    
    %% Create Category (Admin)
    Client->>AdminCategoryController: POST /api/admin/categories
    AdminCategoryController->>CategoryService: createCategory(request)
    CategoryService->>Repository: save(category)
    CategoryService-->>AdminCategoryController: CategoryDto
    AdminCategoryController-->>Client: 201 Created
    
    %% Get Products by Category
    Client->>CategoryController: GET /api/categories/{id}/products
    CategoryController->>CategoryService: getProductsByCategory(id, pageable)
    CategoryService->>Repository: findProductsByCategoryId()
    CategoryService-->>CategoryController: Page<ProductSummaryDto>
    CategoryController-->>Client: 200 OK
```

## Search Operations Flow

```mermaid
sequenceDiagram
    participant Client
    participant SearchController
    participant SearchService
    participant ElasticsearchClient
    participant ProductRepo
    
    %% Search Products
    Client->>SearchController: GET /api/search
    SearchController->>SearchService: searchProducts(request)
    SearchService->>ElasticsearchClient: search(query, filters)
    ElasticsearchClient-->>SearchService: SearchResponse
    SearchService->>SearchService: mapResults()
    SearchService-->>SearchController: SearchResultDto
    SearchController-->>Client: 200 OK
    
    %% Get Search Suggestions
    Client->>SearchController: GET /api/search/suggestions
    SearchController->>SearchService: getSearchSuggestions(request)
    SearchService->>ElasticsearchClient: suggest(prefix)
    ElasticsearchClient-->>SearchService: SuggestResponse
    SearchService-->>SearchController: List<SuggestionDto>
    SearchController-->>Client: 200 OK
    
    %% Get Search Facets
    Client->>SearchController: GET /api/search/facets
    SearchController->>SearchService: getSearchFacets(request)
    SearchService->>ElasticsearchClient: aggregate(filters)
    ElasticsearchClient-->>SearchService: AggregationResponse
    SearchService->>SearchService: mapAggregations()
    SearchService-->>SearchController: List<FacetDto>
    SearchController-->>Client: 200 OK
```

## Category REST Controller (`CategoryController.java`)
- Endpoints for retrieving the category tree, getting products by category.
- Publicly accessible.

## Admin Category REST Controller (`AdminCategoryController.java`)
- Endpoints for creating, updating, deleting categories, assigning products to categories.
- Secured with admin role checks.

## Search REST Controller (`SearchController.java`)
- Endpoints for performing product searches, getting search suggestions, retrieving filter facets.
- Publicly accessible.

## Request DTOs
- `CreateCategoryRequest` (Admin: name, slug, description, parentId, imageUrl)
- `UpdateCategoryRequest` (Admin: name, slug, description, parentId, imageUrl)
- `AssignProductCategoryRequest` (Admin: productId, categoryId)
- `SearchRequest` (query, filters map, sort field, sort direction, page, size)
- `SuggestionRequest` (prefix)

## Response DTOs
- `CategoryDto` (id, name, slug, children list)
- `CategoryDetailDto` (Includes description, parent, attributes)
- `ProductSummaryDto` (Used in category product listing and search results)
- `SearchResultDto` (List<ProductSummaryDto>, pagination info, facets)
- `SuggestionDto` (List<String>)
- `FacetDto` (filterName, List<FacetValueDto>)
- `FacetValueDto` (value, count)

## Exception Handling
- Handling `CategoryNotFoundException`.
- Handling `SearchEngineUnavailableException`.
- Handling invalid search/filter parameters.
- Handling authorization failures for admin endpoints.
