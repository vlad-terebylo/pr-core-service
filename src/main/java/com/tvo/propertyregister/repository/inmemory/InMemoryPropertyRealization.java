package com.tvo.propertyregister.repository.inmemory;

import com.tvo.propertyregister.model.owner.FamilyStatus;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.model.property.PropertyCondition;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.PropertyRepository;

import java.time.LocalDate;
import java.util.List;

public class InMemoryPropertyRealization implements PropertyRepository {

    Property propertyFirst = new Property(
            1, PropertyType.FLAT, "Prague", "Heroev Street 24",
            70, 3, 420, 500000,
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.GOOD);
    Property propertySecond = new Property(2, PropertyType.HOUSE, "Prague", "Boris Niemcov Street 220",
            150, 5, 1200, 750000,
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.GOOD);
    Property propertyThird = new Property(3, PropertyType.OFFICE, "Prague", "Evropska 6",
            300, 10, 3900, 1000000,
            LocalDate.of(2023, 4, 10),
            LocalDate.of(2023, 1, 9),
            PropertyCondition.GOOD);

    Owner ownerFirst = new Owner(1, "John", "Smith",
            30, FamilyStatus.SINGLE,
            false, "johnsmith@gmail.com",
            "+456987123",
            LocalDate.of(1994, 8, 9),
            0, List.of(propertyFirst));
    Owner ownerSecond = new Owner(2, "Linda", "Johnson",
            31, FamilyStatus.MARRIED,
            true, "lindajohnson@gmail.com",
            "+789456147",
            LocalDate.of(1993, 7, 17),
            0, List.of(propertySecond));
    Owner ownerThird = new Owner(3, "Dan", "Kravets",
            30, FamilyStatus.SINGLE,
            false, "DDD_JDK@gmail.com",
            "+784578457",
            LocalDate.of(1994, 1, 31),
            0, List.of(propertyThird));

    private List<Owner> owners = List.of(ownerFirst, ownerSecond, ownerThird);
    private List<Property> properties = List.of(propertyFirst, propertySecond, propertyThird);

    @Override
    public List<Property> getAllProperties(int ownerId) {
        for (Owner currentOwner : this.owners) {
            if (currentOwner.getId() == ownerId) {
                return currentOwner.getProperties();
            }
        }

        return null;
    }

    @Override
    public void save(int ownerId, Property property) {
        for (Owner currentOwner : this.owners) {
            if (currentOwner.getId() == ownerId) {
                currentOwner.getProperties().add(property);
            }
        }
    }

    @Override
    public void update(int ownerId, int propertyId, Property property) {
        for (Owner currentOwner : this.owners) {
            if (currentOwner.getId() == ownerId) {
                for (Property currentProperty : currentOwner.getProperties()) {
                    if (currentProperty.getId() == propertyId) {
                        currentProperty.setNumberOfRooms(property.getNumberOfRooms());
                        currentProperty.setCost(property.getCost());
                        currentProperty.setTaxesForProperty(property.getTaxesForProperty());
                        currentProperty.setDateOfBecomingOwner(property.getDateOfBecomingOwner());
                    }
                }
            }
        }
    }

    @Override
    public void remove(int ownerId, int propertyId) {
        for (Owner currentOwner : this.owners) {
            if (currentOwner.getId() == ownerId) {
                currentOwner.getProperties().removeIf(currentProperty -> currentProperty.getId() == propertyId);
            }
        }
    }

}
