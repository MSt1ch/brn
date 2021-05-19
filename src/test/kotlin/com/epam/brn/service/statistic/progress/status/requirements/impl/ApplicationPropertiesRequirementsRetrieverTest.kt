package com.epam.brn.service.statistic.progress.status.requirements.impl

import com.epam.brn.dto.statistic.StatusRequirements
import com.epam.brn.dto.statistic.UserExercisingPeriod
import com.epam.brn.dto.statistic.UserExercisingProgressStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.env.Environment
import javax.naming.OperationNotSupportedException
import kotlin.test.assertEquals

/**
 * @author Nikolai Lazarev
 */

@ExtendWith(MockitoExtension::class)
internal class ApplicationPropertiesRequirementsRetrieverTest {

    @InjectMocks
    private lateinit var retrieverApplicationProperties: ApplicationPropertiesRequirementsRetriever

    @Mock
    private lateinit var env: Environment
    private val basePath = "brn.statistic.progress"

    @Test
    fun `getRequirementsForStatus should return requirements for BAD status`() {
        // GIVEN
        val status = UserExercisingProgressStatus.BAD
        val period = UserExercisingPeriod.DAY
        val minimalRequirements = 0
        val maximalRequirements = 15
        val periodName = period.name.toLowerCase()
        val statusName = status.name.toLowerCase()
        `when`(env.getProperty("$basePath.$periodName.status.$statusName.maximal")).thenReturn(maximalRequirements.toString())
        `when`(env.getProperty("$basePath.$periodName.status.$statusName.minimal")).thenReturn(minimalRequirements.toString())
        val expectedRequirements = StatusRequirements(
            status = status,
            minimalRequirements = minimalRequirements,
            maximalRequirements = maximalRequirements
        )

        // WHEN
        val requirementsForStatus = retrieverApplicationProperties.getRequirementsForStatus(status, period)

        // THEN
        assertEquals(expectedRequirements, requirementsForStatus)
    }

    @Test
    fun `getRequirementsForStatus should throw OperationNotSupportedException when unsupported status passed`() {
        // GIVEN
        val period = UserExercisingPeriod.DAY
        val status = UserExercisingProgressStatus.GOOD
        val maximalRequirements = 15
        val periodName = period.name.toLowerCase()
        val statusName = status.name.toLowerCase()
        `when`(env.getProperty("$basePath.$periodName.status.$statusName.maximal")).thenReturn(maximalRequirements.toString())
        `when`(env.getProperty("$basePath.$periodName.status.$statusName.minimal")).thenReturn(null)

        // THEN
        assertThrows<OperationNotSupportedException> {
            retrieverApplicationProperties.getRequirementsForStatus(status, period)
        }
    }
}
