/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.models.utilities;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* Represents the pagination and sorting options for a paginated request.
*
* @param page          The number of the page to retrieve (required).
* @param size          The number of items per page (default is 50).
* @param sort          The field to be used for sorting the results (optional), and The direction in which to sort the results, either ascending ("asc") or descending ("desc") (default is "asc").
* @param filters       The field to be used for search the results (optional), and a search term to filter the results (optional).
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {

    private int page;
    private int size;
    private List<Map<String, String>> sort;
    private Map<String, String> filters;

}
