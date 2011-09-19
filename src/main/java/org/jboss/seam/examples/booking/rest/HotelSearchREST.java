package org.jboss.seam.examples.booking.rest;


import java.util.List;


import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;

import org.jboss.logging.Logger;
import org.jboss.seam.examples.booking.inventory.SearchCriteria;
import org.jboss.seam.examples.booking.model.Hotel;
import org.jboss.seam.examples.booking.model.Hotel_;


@Path("/hotels")
public class HotelSearchREST {
    
    
    @PersistenceContext
    private EntityManager em;
    @Inject
    private Logger log;
    
    private SearchCriteria criteria;
    
	@GET
	@Produces("application/json")
	public List<Hotel> findAll() {
		// currently assumes fetch all hotels in database
		criteria = new SearchCriteria();
		criteria.firstPage();
		criteria.setPageSize(1000);
		criteria.setQuery("%");
		return queryHotels(criteria);
		
	}
	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Hotel findHotel(@PathParam("id") long id) {
		return em.find(Hotel.class,id);		
	}
    private List<Hotel> queryHotels(final SearchCriteria criteria) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Hotel> cquery = builder.createQuery(Hotel.class);
        Root<Hotel> hotel = cquery.from(Hotel.class);
        // QUESTION can like create the pattern for us?
        cquery.select(hotel).where(
                builder.or(builder.like(builder.lower(hotel.get(Hotel_.name)), criteria.getSearchPattern()),
                        builder.like(builder.lower(hotel.get(Hotel_.city)), criteria.getSearchPattern()),
                        builder.like(builder.lower(hotel.get(Hotel_.zip)), criteria.getSearchPattern()),
                        builder.like(builder.lower(hotel.get(Hotel_.address)), criteria.getSearchPattern())));

        List<Hotel> results = em.createQuery(cquery).getResultList();
        

        log.info("Found " + results.size());
        return results;
    }
	
}
