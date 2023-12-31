/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.OpenmrsObject;
import org.openmrs.Visit;
import org.springframework.util.ReflectionUtils;

/**
 * Tests the {@link Reflect} class.
 */
public class ReflectTest {
	
	/**
	 * @see Reflect#hasField(Field)
	 */
	@Test
	public void xhasField_shouldReturnTrueIfGivenFieldIsDeclaredInParameterizedClassOrSubClass() {
		Reflect reflect = new Reflect(OpenmrsObject.class);
		List<Field> allFields = Reflect.getAllFields(OpenmrsObjectImp.class);
		
		Field genericCollectionField = findFieldByName(allFields, "genericCollectionField");
		assertTrue(reflect.hasField(genericCollectionField));
	}
	
	/**
	 * @see Reflect#hasField(Field)
	 */
	@Test
	public void xhasField_shouldReturnFalseIfGivenFieldIsNotDeclaredInParameterizedClassOrItsSubClass() {
		Reflect reflect = new Reflect(OpenmrsObject.class);
		List<Field> allFields = Reflect.getAllFields(OpenmrsObjectImp.class);
		
		Field normalClassField = findFieldByName(allFields, "normalClassField");
		
		assertFalse(reflect.hasField(normalClassField));
	}
	
	private Field findFieldByName(List<Field> fields, String name) {
		for (Field field : fields) {
			if (name.equals(field.getName())) {
				return field;
			}
		}
		throw new IllegalArgumentException("Field not found!");
	}
	
	/**
	 * @see Reflect#getAllFields(Class<*>)
	 */
	@Test
	public void getAllFields_shouldReturnAllFieldsIncludePrivateAndSuperClasses() {
		List<Field> allFields = Reflect.getAllFields(OpenmrsObjectImp.class);
		
		findFieldByName(allFields, "subClassField");
		findFieldByName(allFields, "normalClassField");
		findFieldByName(allFields, "nonCollectionField");
		findFieldByName(allFields, "genericCollectionField");
	}
	
	/**
	 * @see Reflect#isCollection(Class<*>)
	 */
	@Test
	public void isCollection_shouldReturnFalseIfGivenFieldClassIsNotACollectionClass() {
		Reflect reflect = new Reflect(OpenmrsObject.class);
		List<Field> allFields = Reflect.getAllFields(OpenmrsObjectImp.class);
		
		Field nonCollectionField = findFieldByName(allFields, "nonCollectionField");
		assertFalse(reflect.isCollectionField(nonCollectionField));
	}
	
	/**
	 * @see Reflect#isCollection(Class<*>)
	 */
	@Test
	public void isCollection_shouldReturnTrueIfGivenFieldClassIsCollectionClass() {
		assertTrue(Reflect.isCollection(ArrayList.class));
	}
	
	/**
	 * @see Reflect#isCollection(Object)
	 */
	@Test
	public void isCollection_shouldReturnFalseIfGivenObjectIsNotACollection() {
		assertFalse(Reflect.isCollection(new NormalClass()));
	}
	
	/**
	 * @see Reflect#isCollection(Object)
	 */
	@Test
	public void isCollection_shouldReturnTrueIfGivenObjectIsCollectionClass() {
		assertTrue(Reflect.isCollection(new ArrayList<>()));
	}
	
	/**
	 * @see Reflect#Reflect(Class)
	 */
	@Test
	public void Reflect_shouldThrowExceptionWhenNullIsPassed() {
		assertThrows(NullPointerException.class, () -> new Reflect(null));
	}
	
	/**
	 * @see Reflect#getInheritedFields(Class)
	 */
	@Test
	public void getInheritedFields_shouldReturnOnlyTheSubClassFieldsOfGivenParameterizedClass() {
		Reflect reflect = new Reflect(OpenmrsObject.class);
		List<Field> fields = reflect.getInheritedFields(OpenmrsObjectImp.class);
		
		List<Field> allFields = Reflect.getAllFields(OpenmrsObjectImp.class);
		
		findFieldByName(fields, "subClassField");
		findFieldByName(fields, "nonCollectionField");
		findFieldByName(fields, "genericCollectionField");
		
		Field normalClassField = findFieldByName(allFields, "normalClassField");
		assertFalse(fields.contains(normalClassField));
	}
	
	/**
	 * @see Reflect#isCollectionField(Field)
	 */
	@Test
	public void isCollectionField_shouldReturnTrueIfGivenFieldIsCollectionAndItsElementTypeIsGivenParameterized()
	{
		Reflect reflect = new Reflect(OpenmrsObject.class);
		List<Field> allFields = Reflect.getAllFields(OpenmrsObjectImp.class);
		
		assertEquals("subClassField", allFields.get(0).getName());
		assertTrue(reflect.isCollectionField(allFields.get(0)));
	}
	
	/**
	 * @see Reflect#isCollectionField(Field)
	 */
	@Test
	public void isCollectionField_shouldReturnFalseIfGivenFieldIsNotACollection() {
		assertFalse(Reflect.isCollection(NormalClass.class));
	}
	
	/**
	 * @see Reflect#isCollectionField(Field)
	 */
	@Test
	public void isCollectionField_shouldReturnFalseIfGivenFieldIsCollectionAndElementTypeIsOtherThanGiven() {
		Reflect reflect = new Reflect(OpenmrsObject.class);
		List<Field> allFields = Reflect.getAllFields(OpenmrsObjectImp.class);
		
		Field genericCollectionField = findFieldByName(allFields, "genericCollectionField");
		assertFalse(reflect.isCollectionField(genericCollectionField));
	}
	
	/**
	 * @see Reflect#isSuperClass(Class)
	 */
	@Test
	public void isSuperClass_shouldReturnFalseIfGivenSubClassIsNotAccessibleFromGivenParameterizedClass() {
		Reflect reflect = new Reflect(OpenmrsObject.class);
		
		assertFalse(reflect.isSuperClass(new NormalClass()));
	}
	
	/**
	 * @see Reflect#isSuperClass(Class)
	 */
	@Test
	public void isSuperClass_shouldReturnTrueIfGivenSubClassIsAccessibleFromGivenParameterizedClass() {
		Reflect reflect = new Reflect(OpenmrsObject.class);
		
		assertTrue(reflect.isSuperClass(OpenmrsObjectImp.class));
	}
	
	/**
	 * @see Reflect#isSuperClass(Object)
	 */
	@Test
	public void isSuperClass_shouldReturnFalseIfGivenObjectIsNotAccessibleFromGivenParameterizedClass() {
		Reflect reflect = new Reflect(OpenmrsObject.class);
		
		assertFalse(reflect.isSuperClass(NormalClass.class));
	}
	
	/**
	 * @see Reflect#isSuperClass(Object)
	 */
	@Test
	public void isSuperClass_shouldReturnTrueIfGivenObjectIsAccessibleFromGivenParameterizedClass() {
		Reflect reflect = new Reflect(OpenmrsObject.class);
		
		assertTrue(reflect.isSuperClass(new OpenmrsObjectImp()));
	}
	
	/**
	 * @see Reflect#isSuperClass(Type)
	 */
	@Test
	public void isSuperClass_shouldReturnTrueForAGenericWhoseBoundIsASubclass() {
		Reflect reflect = new Reflect(OpenmrsObject.class);
		Field field = ReflectionUtils.findField(Visit.class, "attributes");
		ParameterizedType setOfAttr = (ParameterizedType) field.getGenericType();
		Type genericType = setOfAttr.getActualTypeArguments()[0];
		assertTrue(reflect.isSuperClass(genericType));
	}
	
	/**
	 * @see Reflect#isSuperClass(Type)
	 */
	@Test
	public void isSuperClass_shouldReturnFalseForAGenericWhoseBoundIsNotASubclass() {
		Reflect reflect = new Reflect(Number.class);
		Field field = ReflectionUtils.findField(Visit.class, "attributes");
		ParameterizedType setOfAttr = (ParameterizedType) field.getGenericType();
		Type genericType = setOfAttr.getActualTypeArguments()[0];
		assertFalse(reflect.isSuperClass(genericType));
	}
	
}

class NormalClass {
	
	private String normalClassField;
	
	public String getNormalClassField() {
		return normalClassField;
	}
	
	public void setNormalClassField(String normalClassField) {
		this.normalClassField = normalClassField;
	}
}

class OpenmrsObjectImp extends NormalClass implements OpenmrsObject {
	
	protected Collection<BaseOpenmrsObject> subClassField;
	
	@SuppressWarnings("unused")
	private String nonCollectionField;
	
	Collection genericCollectionField;
	
	@Override
	public Integer getId() {
		return null;
	}
	
	@Override
	public String getUuid() {
		return null;
	}
	
	@Override
	public void setId(Integer id) {
	}
	
	@Override
	public void setUuid(String uuid) {
	}
	
}
