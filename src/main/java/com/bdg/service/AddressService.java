package com.bdg.service;

import com.bdg.converter.model_to_persistance.ModToPerAddress;
import com.bdg.converter.persistent_to_model.PerToModAddress;
import com.bdg.hibernate.HibernateUtil;
import com.bdg.model.AddressMod;
import com.bdg.persistent.AddressPer;
import com.bdg.persistent.PassengerPer;
import com.bdg.repository.AddressRepository;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;

//TODO Sessianeri pahy petq a arvi sax methodnerum(get anel u close anel amen mi methodum)
// check() method-neri hamar sarqel arandzin Validator class(hamarya sax service-nerum nuyn check-ery grum enq)
public class AddressService implements AddressRepository {

    private Session session;
    private static final ModToPerAddress MOD_TO_PER = new ModToPerAddress();
    private static final PerToModAddress PER_TO_MOD = new PerToModAddress();


    @Override
    public AddressMod getBy(int id) {
        checkId(id);

        session = HibernateUtil.getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            AddressPer addressPer = session.get(AddressPer.class, id);
            if (addressPer == null) {
                transaction.rollback();
                return null;
            }

            transaction.commit();
            return PER_TO_MOD.getModelFrom(addressPer);
        } catch (HibernateException e) {
            assert transaction != null;
            transaction.rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }


    @Override
    public Set<AddressMod> getAll() {
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            TypedQuery<AddressPer> query = session.createQuery("FROM AddressPer", AddressPer.class);

            List<AddressPer> addressPerList = query.getResultList();
            if (addressPerList.isEmpty()) {
                transaction.rollback();
                return null;
            }

            transaction.commit();
            return (Set<AddressMod>) PER_TO_MOD.getModelListFrom(addressPerList);
        } catch (HibernateException e) {
            assert transaction != null;
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }


    @Override
    public Set<AddressMod> get(int offset, int perPage, String sort) {
        if (offset <= 0 || perPage <= 0) {
            throw new IllegalArgumentException("Passed non-positive value as 'offset' or 'perPage': ");
        }
        if (sort == null || sort.isEmpty()) {
            throw new IllegalArgumentException("Passed null or empty value as 'sort': ");
        }
        if (!sort.equals("id") && !sort.equals("country") && !sort.equals("city")) {
            throw new IllegalArgumentException("Parameter 'sort' must be 'id' or 'country' or 'city': ");
        }

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            TypedQuery<AddressPer> query = session.createQuery("FROM AddressPer order by " + sort);
            query.setFirstResult(offset);
            query.setMaxResults(perPage);

            List<AddressPer> addressPerList = query.getResultList();
            if (addressPerList.isEmpty()) {
                transaction.rollback();
                return null;
            }

            transaction.commit();
            return (Set<AddressMod>) PER_TO_MOD.getModelListFrom(addressPerList);
        } catch (HibernateException e) {
            assert transaction != null;
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }


    @Override
    public AddressMod save(AddressMod item) {
        checkNull(item);
        if (exists(item)) {
            System.out.println("[" + item.getCountry() + ", " + item.getCity() + "] address already exists: ");
            return null;
        }

        AddressPer addressPer = MOD_TO_PER.getPersistentFrom(item);

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            session.save(addressPer);
            item.setId(addressPer.getId());

            transaction.commit();
            return item;
        } catch (HibernateException e) {
            assert transaction != null;
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean updateBy(int id, AddressMod item) {
        checkId(id);
        checkNull(item);

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            AddressPer addressPer = session.get(AddressPer.class, id);

            if (addressPer == null) {
                transaction.rollback();
                return false;
            }

            addressPer.setCity(item.getCity());
            addressPer.setCountry(item.getCountry());
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            assert transaction != null;
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteBy(int id) {
        checkId(id);

        if (existsPassengerBy(id)) {
            System.out.println("First remove address by " + id + " in passenger table: ");
            return false;
        }

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            AddressPer addressPer = session.get(AddressPer.class, id);
            if (addressPer == null) {
                transaction.rollback();
                return false;
            }

            session.delete(addressPer);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            assert transaction != null;
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }


    private void checkId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("'id' must be a positive number: ");
        }
    }


    private void checkNull(Object item) {
        if (item == null) {
            throw new NullPointerException("Passed null value as 'item': ");
        }
    }


    private boolean existsPassengerBy(int addressId) {
        checkId(addressId);

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            String hql = "select p from PassengerPer as p where p.address = :addressId";
            TypedQuery<PassengerPer> query = session.createQuery(hql);
            query.setParameter("addressId", addressId);

            transaction.commit();
            return !query.getResultList().isEmpty();
        } catch (HibernateException e) {
            assert transaction != null;
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }


    public boolean exists(AddressMod addressMod) {
        checkNull(addressMod);

        for (AddressMod addressModTemp : getAll()) {
            if (addressModTemp.equals(addressMod)) {
                return true;
            }
        }
        return false;
    }


    public int getId(AddressMod addressMod) {
        checkNull(addressMod);

        for (AddressMod addressModTemp : getAll()) {
            if (addressModTemp.equals(addressMod)) {
                return addressModTemp.getId();
            }
        }
        return -1;
    }
}