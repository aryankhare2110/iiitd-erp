package edu.univ.erp. service;

import edu.univ.erp.domain.ComponentType;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter. api.Assertions.*;

@TestMethodOrder(MethodOrderer. OrderAnnotation.class)
class FacultyServiceTest {

    private static FacultyService facultyService;

    @BeforeAll
    static void setUp() {
        facultyService = new FacultyService();
        System.out.println("Starting FacultyService tests...");
    }

    @Test
    @Order(1)
    @DisplayName("Test: Get All Component Types")
    void testGetAllComponentTypes() {
        List<ComponentType> types = facultyService.getAllComponentTypes();

        assertNotNull(types, "Component types list should not be null");
        assertTrue(types.size() > 0, "Should have at least one component type");

        System.out.println("Found component types: " + types. size());
    }

    @Test
    @Order(2)
    @DisplayName("Test: Get Component Type Name")
    void testGetComponentTypeName() {
        String typeName = facultyService.getComponentTypeName(1);

        assertNotNull(typeName, "Component type name should not be null");
        assertFalse(typeName.equals("Unknown"), "Component type should exist");
    }

    @Test
    @Order(3)
    @DisplayName("Test: Check Maintenance Mode")
    void testIsMaintenanceMode() {
        boolean maintenanceMode = facultyService.isMaintenanceMode();

        // Should return boolean value
        System.out.println("Maintenance mode: " + maintenanceMode);
    }

    @Test
    @Order(4)
    @DisplayName("Test: Get Grade From Score")
    void testGetGradeFromScore() {
        assertEquals("A", facultyService.getGradeFromScore(95), "95 should be A");
        assertEquals("A-", facultyService.getGradeFromScore(87), "87 should be A-");
        assertEquals("B", facultyService.getGradeFromScore(82), "82 should be B");
        assertEquals("F", facultyService.getGradeFromScore(45), "45 should be F");
    }

    @AfterAll
    static void tearDown() {
        System.out. println("FacultyService tests completed.");
    }
}