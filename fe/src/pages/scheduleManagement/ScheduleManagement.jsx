import React from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import viLocale from "@fullcalendar/core/locales/vi";
import ReactDOM from "react-dom/client";
import Select from "react-select";
import { useState, useEffect, useRef } from "react";
import "./ScheduleManagement.css";
import { getAll } from "~/services/TeamService";
import { getTeamCalendar } from "~/services/AttendanceService";
import Detail from "./Detail";
import Edit from "./Edit";
import { toast } from "react-toastify";

const ScheduleManagement = () => {
  const [teams, setTeams] = useState([]);
  const [details, setDetails] = useState([]);
  const [showDetailForm, setShowDetailForm] = useState(false);
  const [showEditForm, setShowEditForm] = useState(false);
  const [teamSelect, setTeamSelect] = useState();
  const [teamCalendars, setTeamCalendars] = useState([]);
  const [reloadKey, setReloadKey] = useState(0);
  const rootRef = useRef(null);

  useEffect(() => {
    const fetchData = async () => {
      const teams = await getAll();
      setTeams(teams);
    };
    fetchData();
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      const data = await getTeamCalendar(teamSelect);
      setTeamCalendars(data);
    };
    fetchData();
  }, [teamSelect, reloadKey]);

  const teamOptions = teams.map((d) => ({
    value: d.id,
    label: `${d.name} - ${d.internshipProgramName}`,
  }));

  const events = (teamCalendars || []).map((a) => ({
    start: `${a.date}T${a.timeStart}`,
    end: `${a.date}T${a.timeEnd}`,
    extendedProps: {
      details: a.detailTeamSchedules,
      date: a.date,
    },
  }));

  useEffect(() => {
    const container = document.querySelector(".fc-selectButton-button");
    if (!container) return;

    if (!rootRef.current) {
      rootRef.current = ReactDOM.createRoot(container);
    }

    rootRef.current.render(
      <Select
        placeholder="Chọn nhóm"
        options={teamOptions}
        onChange={(option) => setTeamSelect(option.value)}
        styles={{
          container: (base) => ({ ...base, width: 180 }),
          control: (base) => ({ ...base, minHeight: 30 }),
        }}
      />
    );
  }, [teams]);

  const showDetail = (detail) => {
    setDetails(detail);
    setShowDetailForm(true);
  };

  const showEdit = () => {
    if (teamSelect != null) {
      setShowEditForm(true);
    } else {
      toast.error("Bạn chưa chọn nhóm");
    }
  };

  return (
    <div style={{ padding: "20px", height: "100%" }}>
      <FullCalendar
        plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
        initialView="timeGridWeek"
        headerToolbar={{
          left: "prev,next today",
          center: "title",
          right: "selectButton createButton",
        }}
        titleFormat={() => "LỊCH LÀM VIỆC"}
        allDaySlot={false}
        events={events}
        locale={viLocale}
        height="100%"
        slotMinTime="05:00:00"
        slotMaxTime="19:00:00"
        eventColor="rgba(32, 152, 199, 0.7)"
        eventClick={(a) => {
          showDetail(a.event.extendedProps);
        }}
        customButtons={{
          createButton: {
            text: "Sửa lịch nhóm",
            click: () => {
              showEdit();
            },
          },

          selectButton: {},
        }}
      />

      {showDetailForm && (
        <Detail details={details} onClose={() => setShowDetailForm(false)} />
      )}

      {showEditForm && (
        <Edit
          onClose={(isChanged) => {
            setShowEditForm(false);
            if (isChanged) setReloadKey((prev) => prev + 1);
          }}
          teamSelect={teamSelect}
        />
      )}
    </div>
  );
};

export default ScheduleManagement;
