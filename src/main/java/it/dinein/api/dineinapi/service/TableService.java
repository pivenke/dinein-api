package it.dinein.api.dineinapi.service;

import it.dinein.api.dineinapi.exception.RestaurantNotFoundException;
import it.dinein.api.dineinapi.exception.TabletNotFoundException;
import it.dinein.api.dineinapi.model.Hotelier;
import it.dinein.api.dineinapi.model.Table;
import it.dinein.api.dineinapi.repository.TableRepository;
import it.dinein.api.dineinapi.service.implementation.HotelierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TableService {

    @Autowired
    private TableRepository tableRepository;
    @Autowired
    private HotelierService hotelierService;

    public Table addHotelTable(String hotelName) throws RestaurantNotFoundException {
        Hotelier hotel = hotelierService.findHotelierByRestaurantName(hotelName);
        if (hotel == null) {
            throw new RestaurantNotFoundException("Hotel not found with name: " + hotelName);
        }

        Table table = new Table();
        table.setAvailabilityStatus(true);
        table.setHotelTable(hotel);
        return tableRepository.save(table);
    }

    public void removeTable(Long tableId) throws Exception {
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + tableId));

        if (table.isAvailabilityStatus())
        {
            tableRepository.deleteById(tableId);
        }
        else
        {
            throw new Exception("This table is currently reserved!");
        }
    }

    public List<Table> getTables(String hotelName) throws Exception {
        return tableRepository.findTableIdsByHotelName(hotelName);
    }

    public List<Long> getAvailableTables(String hotelName) throws RestaurantNotFoundException {
        Hotelier hotel = hotelierService.findHotelierByRestaurantName(hotelName);
        if (hotel == null) {
            throw new RestaurantNotFoundException("Hotel not found with name: " + hotelName);
        }

        return tableRepository.findAvailableTableIdsByHotelName(hotelName);
    }

    public Table getTableById(Long id) throws TabletNotFoundException {
        return tableRepository.findById(id)
                .orElseThrow(() -> new TabletNotFoundException("Table not found with id " + id));
    }

}
