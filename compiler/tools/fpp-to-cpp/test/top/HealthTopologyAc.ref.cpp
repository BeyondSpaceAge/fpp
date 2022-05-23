// ======================================================================
// \title  HealthTopologyAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for Health topology
// ======================================================================

#include "HealthTopologyAc.hpp"

namespace M {

  namespace {

    // ----------------------------------------------------------------------
    // Component configuration objects
    // ----------------------------------------------------------------------

    namespace ConfigObjects {

      namespace health {
        Svc::Health::PingEntry pingEntries[] = {
          {
            PingEntries::c1::WARN,
            PingEntries::c1::FATAL,
            "c1"
          },
          {
            PingEntries::c2::WARN,
            PingEntries::c2::FATAL,
            "c2"
          },
        };
      }

    }

  }

  // ----------------------------------------------------------------------
  // Component instances
  // ----------------------------------------------------------------------

  C c1(FW_OPTIONAL_NAME("c1"));

  C c2(FW_OPTIONAL_NAME("c2"));

  Svc::Health health(FW_OPTIONAL_NAME("health"));

  // ----------------------------------------------------------------------
  // Helper functions
  // ----------------------------------------------------------------------

  void initComponents(const TopologyState& state) {
    c1.init(InstanceIds::c1);
    c2.init(InstanceIds::c2);
    health.init(InstanceIds::health);
  }

  void setBaseIds() {
    health.setIdBase(BaseIds::health);
    c1.setIdBase(BaseIds::c1);
    c2.setIdBase(BaseIds::c2);
  }

  void connectComponents() {

    // Health
    c1.set_pingOut_OutputPort(
        0,
        health.get_pingIn_InputPort(0)
    );
    c2.set_pingOut_OutputPort(
        0,
        health.get_pingIn_InputPort(1)
    );
    health.set_pingOut_OutputPort(
        0,
        c1.get_pingIn_InputPort(0)
    );
    health.set_pingOut_OutputPort(
        1,
        c2.get_pingIn_InputPort(0)
    );
  }

  // ----------------------------------------------------------------------
  // Setup and teardown functions
  // ----------------------------------------------------------------------

  void setup(const TopologyState& state) {
    initComponents(state);
    setBaseIds();
    connectComponents();
  }

  void teardown(const TopologyState& state) {

  }

}
